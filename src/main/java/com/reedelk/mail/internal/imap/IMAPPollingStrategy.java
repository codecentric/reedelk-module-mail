package com.reedelk.mail.internal.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.component.imap.IMAPFlags;
import com.reedelk.mail.internal.PollingStrategy;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.OnMessageEvent;
import com.reedelk.mail.internal.commons.SearchTermBuilder;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.sun.mail.imap.IMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.search.SearchTerm;

import static java.util.Arrays.stream;
import static javax.mail.Flags.Flag;

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

            processPolledMessages(messages);

        } catch (Exception exception) {
            logger.warn(exception.getMessage(), exception);

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

    private void processPolledMessages(Message[] messages) throws Exception {
        if (settings.isBatch()) {
            if (isNotStopped()) processBatchMessages(messages);
        } else {
            for (Message message : messages) {
                if (isNotStopped()) processMessage(message);
            }
        }
    }

    private void processMessage(Message message) throws InterruptedException, MessagingException {
        boolean peek = settings.isPeek();
        if (peek) ((IMAPMessage) message).setPeek(peek);
        // Process each message one at a time. If the processing was successful,
        // then we apply the flags to the message (e.g marking it deleted)
        if(OnMessageEvent.fire(IMAPMailListener.class, listener, message)) {
            applyMessageOnSuccessFlags(message);
        } else {
            applyMessageOnFailureFlags(message);
        }
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

    private void processBatchMessages(Message[] messages) throws Exception {
        boolean peek = settings.isPeek();
        if (peek) stream(messages).forEach(message -> ((IMAPMessage) message).setPeek(peek));
        // Process all messages as batch.
        if(OnMessageEvent.fire(IMAPMailListener.class, listener, messages)) {
            applyMessagesOnSuccessFlags(messages);
        } else {
            applyMessagesOnFailureFlags(messages);
        }
    }

    private void applyMessagesOnFailureFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessageOnSuccessFlags(Message message) throws MessagingException {
        if (settings.isDeleteOnSuccess() || settings.isMarkDeleteOnSuccess()) {
            message.setFlag(Flag.DELETED, true);
        }
    }

    private void applyMessageOnFailureFlags(Message message) throws MessagingException {
        // If failure, we don't wat to mark the message as 'seen'. This is needed so that
        // next time we poll, we can fetch the message again and try to process it another time.
        message.setFlag(Flag.SEEN, false);
    }

    private Store getStore() throws MessagingException {
        IMAPConfiguration configuration = settings.getConfiguration();
        Session session = Session.getInstance(new IMAPProperties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }
}
