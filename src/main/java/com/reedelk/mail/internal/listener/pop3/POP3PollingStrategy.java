package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.POP3Properties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;
import java.util.Optional;

public class POP3PollingStrategy extends AbstractPollingStrategy {

    private final POP3Configuration configuration;
    private final boolean batchEmails;
    private final boolean deleteOnSuccess;

    public POP3PollingStrategy(InboundEventListener eventListener,
                               POP3Configuration configuration,
                               Boolean deleteOnSuccess,
                               Boolean batchEmails) {
        super(eventListener);
        this.configuration = configuration;
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
    }

    @Override
    public void run() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = getFolder(store);
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();

            for (Message message : messages) {
                // process message
                boolean processed = processMessage(message);
                if (processed) {
                    if (deleteOnSuccess) {
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            }

        } catch (Exception exception) {
            // TODO: Maybe log this?
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        } finally {
            CloseableUtils.close(folder);
            CloseableUtils.close(store);
        }
    }

    private Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new POP3Properties(configuration));
        Store store = session.getStore();
        // TODO: If authenticate then connect with authentication.
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    private Folder getFolder(Store store) throws MessagingException {
        // For POP3 the folder is always INBOX.
        return store.getFolder("INBOX");
    }
}
