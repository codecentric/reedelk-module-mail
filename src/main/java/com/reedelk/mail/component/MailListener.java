package com.reedelk.mail.component;

import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.IMAPListenerThread;
import com.reedelk.mail.internal.listener.IMAPMessageListener;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.Group;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.AbstractInbound;
import com.reedelk.runtime.api.exception.ESBException;
import com.sun.mail.imap.IMAPStore;
import org.osgi.service.component.annotations.Component;

import javax.mail.Folder;
import javax.mail.Session;
import java.util.Properties;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = MailListener.class, scope = PROTOTYPE)
public class MailListener extends AbstractInbound {

    @Property("IMAP Connection")
    @Group("General")
    private IMAPConfiguration connectionConfiguration;

    private IMAPListenerThread listenerThread;
    private IMAPStore store;
    private Folder folder;

    @Override
    public void onStart() {
        String username = connectionConfiguration.getUsername(); // or throw
        String password = connectionConfiguration.getPassword(); // or throw
        String host = connectionConfiguration.getHost(); // or throw
        Integer port = connectionConfiguration.getPort(); // or default
        Integer timeout = connectionConfiguration.getTimeout(); // or default
        String folderName = connectionConfiguration.getFolder(); // or default (INBOX)

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.timeout", timeout);

        Session session = Session.getInstance(properties);
        try {
            store = (IMAPStore) session.getStore("imaps");
            store.connect(username, password);

            if (!store.hasCapability("IDLE")) {
                // TODO: Switch to polling instead.
                throw new ESBException("IDLE not supported");
            }

            folder = store.getFolder(folderName);
            folder.addMessageCountListener(new IMAPMessageListener(this));

            listenerThread = new IMAPListenerThread(username, password, this.folder);
            listenerThread.start();

        } catch (Exception exception) {
            // TODO: Log this exception
            CloseableUtils.close(listenerThread);
            CloseableUtils.close(this.folder);
            CloseableUtils.close(store);
        }
    }

    @Override
    public void onShutdown() {
        CloseableUtils.close(listenerThread);
        CloseableUtils.close(folder);
        CloseableUtils.close(store);
    }

    public IMAPConfiguration getConnectionConfiguration() {
        return connectionConfiguration;
    }

    public void setConnectionConfiguration(IMAPConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }
}
