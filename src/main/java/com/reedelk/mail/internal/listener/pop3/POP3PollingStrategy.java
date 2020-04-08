package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.POP3Properties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;

public class POP3PollingStrategy extends AbstractPollingStrategy {

    private final POP3Configuration configuration;

    public POP3PollingStrategy(POP3Configuration configuration, Boolean deleteAfterRetrieve, InboundEventListener eventListener) {
        super(eventListener, deleteAfterRetrieve);
        this.configuration = configuration;
    }

    @Override
    public void poll() {
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
                    if (deleteAfterRetrieve) {
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
