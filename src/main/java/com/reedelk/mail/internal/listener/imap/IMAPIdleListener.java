package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.mail.internal.listener.FireEventOnResult;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.reedelk.runtime.api.exception.ESBException;
import com.sun.mail.imap.IMAPStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static javax.mail.Flags.Flag;

public class IMAPIdleListener {

    private final Logger logger = LoggerFactory.getLogger(IMAPIdleListener.class);

    private final String inboxFolder;
    private final Boolean batchEmails;
    private final Boolean deleteOnSuccess;
    private final IMAPConfiguration configuration;
    private final InboundEventListener eventListener;

    private Folder folder;
    private IMAPStore store;
    private IMAPIdlListenerThread listenerThread;

    public IMAPIdleListener(IMAPMailListener eventListener,
                            IMAPConfiguration configuration,
                            Boolean deleteOnSuccess,
                            Boolean batchEmails) {
        this.configuration = configuration;
        this.eventListener = eventListener;
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
        this.inboxFolder = Optional.ofNullable(configuration.getFolder()).orElse(Defaults.IMAP_FOLDER_NAME);
    }

    public void start() {
        String username = configuration.getUsername();
        String password = configuration.getPassword();
        String host = configuration.getHost();
        Session session = Session.getInstance(new IMAPProperties(configuration));

        try {
            store = (IMAPStore) session.getStore();
            store.connect(host, username, password);

            if (!store.hasCapability("IDLE")) {
                throw new ESBException("IDLE not supported");
            }

            folder = store.getFolder(inboxFolder);
            folder.addMessageCountListener(new MessageAdapter());

            listenerThread = new IMAPIdlListenerThread(host, username, password, this.folder);
            listenerThread.start();

        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        } finally {
            cleanup();
        }
    }

    public void stop() {
        cleanup();
    }

    private void cleanup() {
        CloseableUtils.close(listenerThread);
        CloseableUtils.close(folder, false); // TODO: If DELETE (spunge == true) if MARK AS DELETE (spunge == false)
        CloseableUtils.close(store);
    }

    private class MessageAdapter extends MessageCountAdapter {

        @Override
        public void messagesAdded(MessageCountEvent event) {
            Message[] messages = event.getMessages();

            try {
                if (batchEmails) {
                    boolean success = processMessages(messages);
                    if (success) applyMessagesOnSuccessFlags(messages);
                } else {
                    for (Message message : messages) {
                        try {
                            boolean success = processMessage(message);
                            if (success) applyMessageOnSuccessFlags(message);

                        } catch (Exception exception) {
                            String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                            logger.error(error, exception);
                        }
                    }
                }
            } catch (Exception e) {

            }
        }

        private void applyMessagesOnSuccessFlags(Message[] messages) throws MessagingException {
            for (Message message : messages) {
                applyMessageOnSuccessFlags(message);
            }
        }

        private void applyMessageOnSuccessFlags(Message message) throws MessagingException {
            if (deleteOnSuccess) {
                message.setFlag(Flag.DELETED, true);
            }
        }

        private boolean processMessage(Message message) throws InterruptedException {
            com.reedelk.runtime.api.message.Message inMessage =
                    MailMessageToMessageMapper.map(IMAPMailListener.class, message);
            return fireEventAndWaitResult(inMessage);
        }

        private boolean processMessages(Message[] messages) throws InterruptedException {
            com.reedelk.runtime.api.message.Message inMessage =
                    MailMessageToMessageMapper.map(IMAPMailListener.class, messages);
            return fireEventAndWaitResult(inMessage);
        }

        private boolean fireEventAndWaitResult(com.reedelk.runtime.api.message.Message inMessage) throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            FireEventOnResult fireEvent = new FireEventOnResult(latch);

            eventListener.onEvent(inMessage, fireEvent);

            latch.await(); // TODO: Should we add a timeout here?

            return fireEvent.result();
        }
    }
}
