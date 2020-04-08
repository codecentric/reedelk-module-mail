package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.reedelk.runtime.api.exception.ESBException;
import com.sun.mail.imap.IMAPStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Optional;

import static javax.mail.Flags.Flag;

public class ImapIdleMailListener1 {

    private final Boolean batchEmails;
    private final Boolean deleteOnSuccess;
    private final IMAPConfiguration configuration;
    private final InboundEventListener eventListener;

    private Folder folder;
    private IMAPStore store;
    private IDLEListenerThread listenerThread;

    public ImapIdleMailListener1(IMAPMailListener eventListener,
                                 IMAPConfiguration configuration,
                                 Boolean deleteOnSuccess,
                                 Boolean batchEmails) {
        this.configuration = configuration;
        this.eventListener = eventListener;
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
    }

    public void start() {
        String username = configuration.getUsername(); // or throw
        String password = configuration.getPassword(); // or throw
        String folderName = configuration.getFolder(); // or default (INBOX)

        Session session = Session.getInstance(new IMAPProperties(configuration));
        try {
            store = (IMAPStore) session.getStore();
            store.connect(username, password);

            if (!store.hasCapability("IDLE")) {
                // TODO: Switch to polling instead.
                throw new ESBException("IDLE not supported");
            }

            folder = store.getFolder(folderName);
            folder.addMessageCountListener(new MessageAdapter());

            listenerThread = new IDLEListenerThread(username, password, this.folder);
            listenerThread.start();

        } catch (Exception exception) {
            // TODO: Log this exception
            cleanup();
        }
    }

    public void stop() {
        cleanup();
    }

    private void cleanup() {
        CloseableUtils.close(listenerThread);
        CloseableUtils.close(folder); // TODO: If DELETE (spunge == true) if MARK AS DELETE (spunge == false)
        CloseableUtils.close(store);
    }

    private class MessageAdapter extends MessageCountAdapter {

        public final Logger logger = LoggerFactory.getLogger(MessageAdapter.class);

        @Override
        public void messagesAdded(MessageCountEvent event) {
            Message[] messages = event.getMessages();

            for (Message message : messages) {
                try {
                    // TODO: The message must be set to deleted.
                    boolean success = processMessage(message);
                    if (success) {
                        if (deleteOnSuccess) {
                            message.setFlag(Flag.DELETED, true);
                        }
                    }
                } catch (Exception exception) {
                    String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                    logger.error(error, exception);
                }
            }
        }

        private boolean processMessage(Message message) throws Exception {
            com.reedelk.runtime.api.message.Message inMessage =
                    MailMessageToMessageMapper.map(IMAPMailListener.class, message);
            eventListener.onEvent(inMessage);
            // TODO: Call the listener. ... if process success, (the flow executed correctly)
            // Then ... otherwise wait...
            return true;
        }
    }
}
