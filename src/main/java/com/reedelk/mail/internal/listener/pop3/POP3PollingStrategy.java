package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.POP3Properties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class POP3PollingStrategy extends AbstractPollingStrategy {

    private final POP3Configuration configuration;

    public POP3PollingStrategy(POP3Configuration configuration, Boolean deleteAfterRetrieve, InboundEventListener eventListener) {
        super(eventListener, deleteAfterRetrieve);
        this.configuration = configuration;
    }

    @Override
    protected Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new POP3Properties(configuration));
        Store store = session.getStore();
        // TODO: If authenticate then connect with authentication.
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    @Override
    protected Folder getFolder(Store store) throws MessagingException {
        // For POP3 the folder is always INBOX.
        return store.getFolder("INBOX");
    }

}
