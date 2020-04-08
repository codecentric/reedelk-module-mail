package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class ImapPollingStrategy extends AbstractPollingStrategy {

    private final IMAPConfiguration configuration;

    public ImapPollingStrategy(IMAPConfiguration configuration, InboundEventListener listener) {
        super(listener, false); // TODO: Fixme
        this.configuration = configuration;
    }

    @Override
    protected Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new IMAPProperties(configuration));
        Store store = session.getStore();
        // TODO: If authenticate then connect with authentication.
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }

    @Override
    protected Folder getFolder(Store store) throws MessagingException {
        return store.getFolder(configuration.getFolder());
    }
}
