package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.component.POP3MailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.POP3Properties;
import com.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Optional;

public class POP3PollingStrategy extends AbstractPollingStrategy {

    private final Logger logger = LoggerFactory.getLogger(POP3PollingStrategy.class);

    private final POP3Configuration configuration;

    private final boolean batchEmails;
    private final boolean deleteOnSuccess;
    private final boolean markSeenOnSuccess;
    private final Integer limit;

    public POP3PollingStrategy(InboundEventListener eventListener,
                               POP3Configuration configuration,
                               Boolean deleteOnSuccess,
                               Boolean batchEmails,
                               Integer limit,
                               Boolean markSeenOnSuccess) {
        super(eventListener);
        this.configuration = configuration;
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
        this.limit = limit;
        this.markSeenOnSuccess = Optional.ofNullable(markSeenOnSuccess).orElse(false);
    }

    @Override
    public void run() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = store.getFolder(Defaults.POP_FOLDER_NAME);
            folder.open(Folder.READ_WRITE);

            if (Thread.interrupted()) return;

            Message[] messages = folder.getMessages();

            if (Thread.interrupted()) return;

            Message[] toProcess = messages;
            if (limit != null) {
                toProcess = new Message[limit];
                if (limit >= 0) System.arraycopy(messages, 0, toProcess, 0, limit);
            }

            if (batchEmails) {
                boolean success = processMessages(POP3MailListener.class, toProcess);
                if (success) applyMessagesOnSuccessFlags(toProcess);
                else applyMessagesOnFailureFlags(toProcess);

            } else {
                for (Message message : toProcess) {
                    if (Thread.interrupted()) return;
                    // Process each message one at a time. If the processing was successful,
                    // then we apply the flags to the message (e.g marking it deleted)
                    boolean success = processMessage(POP3MailListener.class, message);
                    if (success) applyMessageOnSuccessFlags(message);
                    else applyMessageOnFailureFlags(message);
                }
            }

        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);

        } finally {
            CloseableUtils.close(folder);
            CloseableUtils.close(store);
        }
    }

    private void applyMessageOnSuccessFlags(Message message) throws MessagingException {
        message.setFlag(Flags.Flag.SEEN, markSeenOnSuccess);
        if (deleteOnSuccess) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    // If failure, we don't wat to mark the message as 'seen'
    private void applyMessageOnFailureFlags(Message message) throws MessagingException {
        message.setFlag(Flags.Flag.SEEN, false);
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

    private Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new POP3Properties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }
}
