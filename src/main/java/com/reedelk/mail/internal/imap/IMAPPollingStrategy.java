package com.reedelk.mail.internal.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.component.imap.IMAPFlags;
import com.reedelk.mail.internal.PollingStrategy;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.FlagUtils;
import com.reedelk.mail.internal.commons.OnMessageEvent;
import com.reedelk.mail.internal.commons.SearchTermBuilder;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.sun.mail.imap.IMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.search.SearchTerm;

import static com.reedelk.mail.internal.commons.Messages.MailListenerComponent.POLL_ERROR;
import static java.util.Arrays.stream;

public class IMAPPollingStrategy implements PollingStrategy {

    private final Logger logger = LoggerFactory.getLogger(IMAPPollingStrategy.class);

    private final IMAPPollingStrategySettings settings;
    private final InboundEventListener listener;

    private volatile boolean stopped;

    public IMAPPollingStrategy(InboundEventListener listener, IMAPPollingStrategySettings settings) {
        this.listener = listener;
        this.settings = settings;
    }

    @Override
    public void run() {

        Store store = null;

        Folder folder = null;

        try {
            store = getStore();

            folder = store.getFolder(settings.getFolder());

            folder.open(Folder.READ_WRITE);

            Message[] messages = fetchMessages(folder);

            if (settings.isBatch() && isNotStopped()) {
                processMessages(messages);

            } else {
                for (Message message : messages) {
                    if (isNotStopped()) processMessage(message);
                }
            }

        } catch (Exception exception) {
            String error = POLL_ERROR.format(exception.getMessage());
            logger.error(error, exception);

        } finally {
            // If expunge == false, messages marked as deleted are not obliterated,
            // meaning they are not removed from the folder. Otherwise they are
            // completely removed from the IMAP folder.
            if (settings.isMarkDeleteOnSuccess()) CloseableUtils.close(folder, false);
            if (settings.isDeleteOnSuccess()) CloseableUtils.close(folder, true);
            if (!settings.isMarkDeleteOnSuccess() && !settings.isDeleteOnSuccess()) CloseableUtils.close(folder, false);
            CloseableUtils.close(store);
        }
    }

    @Override
    public synchronized void stop() {
        this.stopped = true;
    }

    private synchronized boolean isNotStopped() {
        return !Thread.interrupted() && !stopped;
    }

    private void processMessage(Message message) {
        boolean peek = settings.isPeek();
        if (peek) ((IMAPMessage) message).setPeek(peek);
        if(OnMessageEvent.fire(IMAPMailListener.class, listener, message)) {
            applyMessageOnSuccessFlags(message);
        } else {
            applyMessageOnFailureFlags(message);
        }
    }

    private void processMessages(Message[] messages) {
        boolean peek = settings.isPeek();
        if (peek) stream(messages).forEach(message -> ((IMAPMessage) message).setPeek(peek));
        // Process all messages as batch.
        if(OnMessageEvent.fire(IMAPMailListener.class, listener, messages)) {
            for (Message message : messages) {
                applyMessageOnSuccessFlags(message);
            }
        } else {
            for (Message message : messages) {
                applyMessageOnSuccessFlags(message);
            }
        }
    }

    private Store getStore() throws MessagingException {
        IMAPConfiguration configuration = settings.getConfiguration();
        Session session = Session.getInstance(new IMAPProperties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    private Message[] fetchMessages(Folder folder) throws MessagingException {
        IMAPFlags matcher = settings.getMatcher();
        int limit = settings.getLimit();

        SearchTerm searchTerm = SearchTermBuilder.from(matcher);
        Message[] messages = folder.search(searchTerm);
        if (messages.length <= limit) return messages;

        Message[] toProcess = new Message[limit];
        System.arraycopy(messages, 0, toProcess, 0, limit);
        return toProcess;
    }

    private void applyMessageOnSuccessFlags(Message message) {
        // Process each message one at a time. If the processing was successful,
        // then we apply the flags to the message (e.g marking it deleted)
        if (settings.isDeleteOnSuccess() || settings.isMarkDeleteOnSuccess()) {
            FlagUtils.deleted(message);
        }
    }

    private void applyMessageOnFailureFlags(Message message) {
        // If failure, we don't wat to mark the message as 'seen'. This is needed so that
        // next time we poll, we can fetch the message again and try to process it another time.
        FlagUtils.notSeen(message);
    }
}
