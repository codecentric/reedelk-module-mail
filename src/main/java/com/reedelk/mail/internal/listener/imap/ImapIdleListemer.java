package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.MailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.mail.internal.listener.MailListenerInterface;
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

public class ImapIdleListemer implements MailListenerInterface {

    private final IMAPConfiguration configuration;
    private final InboundEventListener eventListener;

    private ImapIdleListenerThread listenerThread;
    private IMAPStore store;
    private Folder folder;

    public ImapIdleListemer(IMAPConfiguration configuration, InboundEventListener eventListener) {
        this.configuration = configuration;
        this.eventListener = eventListener;
    }

    @Override
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
            folder.addMessageCountListener(new IMAPMessageListener(eventListener));

            listenerThread = new ImapIdleListenerThread(username, password, this.folder);
            listenerThread.start();

        } catch (Exception exception) {
            // TODO: Log this exception
            CloseableUtils.close(listenerThread);
            CloseableUtils.close(this.folder);
            CloseableUtils.close(store);
        }
    }

    @Override
    public void stop() {
        CloseableUtils.close(listenerThread);
        CloseableUtils.close(folder);
        CloseableUtils.close(store);
    }

    static class IMAPMessageListener extends MessageCountAdapter {

        private static final Logger logger = LoggerFactory.getLogger(IMAPMessageListener.class);

        private final InboundEventListener listener;

        public IMAPMessageListener(InboundEventListener listener) {
            this.listener = listener;
        }

        @Override
        public void messagesAdded(MessageCountEvent event) {
            Message[] messages = event.getMessages();

            for (Message message : messages) {
                try {
                    com.reedelk.runtime.api.message.Message inMessage =
                            MailMessageToMessageMapper.map(MailListener.class, message);
                    listener.onEvent(inMessage);
                } catch (Exception exception) {
                    String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                    logger.error(error, exception);
                }
            }
        }
    }
}
