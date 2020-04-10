package com.reedelk.mail.internal.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.component.POP3MailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.AbstractPollingStrategy;
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
    private final Integer limit;
    private boolean stopped;

    public POP3PollingStrategy(InboundEventListener eventListener,
                               POP3Configuration configuration,
                               Boolean deleteOnSuccess,
                               Boolean batchEmails,
                               Integer limit) {
        super(eventListener);
        this.configuration = configuration;
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
        this.limit = limit;
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

            } else {
                for (Message message : toProcess) {
                    if (Thread.interrupted()) return;
                    if (stopped) return;
                    // Process each message one at a time. If the processing was successful,
                    // then we apply the flags to the message (e.g marking it deleted)
                    boolean success = processMessage(POP3MailListener.class, message);
                    if (success) applyMessageOnSuccessFlags(message);
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
        if (deleteOnSuccess) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private Store getStore() throws MessagingException {
        Session session = Session.getInstance(new POP3Properties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }
}