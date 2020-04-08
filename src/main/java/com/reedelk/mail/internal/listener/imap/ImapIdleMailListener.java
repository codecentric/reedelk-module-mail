package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.reedelk.runtime.api.exception.ESBException;
import com.sun.mail.imap.IMAPStore;

import javax.mail.Folder;
import javax.mail.Session;

public class ImapIdleMailListener implements ProtocolMailListener {

    private final IMAPConfiguration configuration;
    private final InboundEventListener eventListener;

    private IDLListenerThread listenerThread;
    private IMAPStore store;
    private Folder folder;

    public ImapIdleMailListener(IMAPConfiguration configuration, InboundEventListener eventListener) {
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
            folder.addMessageCountListener(new ImapIdleMessageListener(eventListener));

            listenerThread = new IDLListenerThread(username, password, this.folder);
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
}
