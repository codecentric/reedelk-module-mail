package com.reedelk.mail.internal.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.component.POP3MailListener;
import com.reedelk.mail.internal.PollingStrategy;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.commons.FlagUtils;
import com.reedelk.mail.internal.commons.OnMessageEvent;
import com.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;

import static com.reedelk.mail.internal.commons.Messages.MailListenerComponent.POLL_ERROR;

public class POP3PollingStrategy implements PollingStrategy {

    private final Logger logger = LoggerFactory.getLogger(POP3PollingStrategy.class);

    private boolean stopped;
    private final InboundEventListener listener;
    private final POP3PollingStrategySettings settings;

    public POP3PollingStrategy(InboundEventListener listener, POP3PollingStrategySettings settings) {
        this.listener = listener;
        this.settings = settings;
    }

    @Override
    public void run() {
        Store store = null;

        Folder folder = null;

        try {
            store = getStore();

            folder = store.getFolder(Defaults.POP_FOLDER_NAME);

            folder.open(Folder.READ_WRITE);

            Message[] messages = fetchMessages(folder);

            if (settings.isBatch() && isNotStopped()) {

                if(OnMessageEvent.fire(POP3MailListener.class, listener, messages)) {
                    applyMessagesOnSuccessFlags(messages);
                }

            } else {
                for (Message message : messages) {
                    if (isNotStopped() && OnMessageEvent.fire(POP3MailListener.class, listener, message)) {
                        // Process each message one at a time. If the processing was successful,
                        // then we apply the flags to the message (e.g marking it deleted)
                        applyMessageOnSuccessFlags(message);
                    }
                }
            }

        } catch (Exception exception) {
            String error = POLL_ERROR.format(exception.getMessage());
            logger.error(error, exception);

        } finally {
            CloseableUtils.close(folder, settings.isDeleteOnSuccess());
            CloseableUtils.close(store);
        }
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    private Store getStore() throws MessagingException {
        POP3Configuration configuration = settings.getConfiguration();
        Session session = Session.getInstance(new POP3Properties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    private Message[] fetchMessages(Folder folder) throws MessagingException {
        int limit = settings.getLimit();
        Message[] messages = folder.getMessages();
        if (messages.length <= limit) return messages;

        Message[] toProcess = new Message[limit];
        System.arraycopy(messages, 0, toProcess, 0, limit);
        return toProcess;
    }

    private synchronized boolean isNotStopped() {
        return !Thread.interrupted() && !stopped;
    }

    private void applyMessageOnSuccessFlags(Message message) {
        if (settings.isDeleteOnSuccess()) {
            FlagUtils.deleted(message);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }
}
