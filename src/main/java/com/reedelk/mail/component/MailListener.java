package com.reedelk.mail.component;

import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.IMAPListenerThread;
import com.reedelk.mail.internal.listener.IMAPMessageListener;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import com.reedelk.runtime.api.exception.ESBException;
import com.sun.mail.imap.IMAPStore;
import org.osgi.service.component.annotations.Component;

import javax.mail.Folder;
import javax.mail.Session;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = MailListener.class, scope = PROTOTYPE)
public class MailListener extends AbstractInbound {

    @Property("Protocol")
    @Group("General")
    private Protocol protocol;

    @Property("POP3 Connection")
    @Group("General")
    @When(propertyName = "protocol", propertyValue = "POP3")
    private POP3Configuration pop3Configuration;

    @Property("IMAP Connection")
    @Group("General")
    @When(propertyName = "protocol", propertyValue = "IMAP")
    @When(propertyName = "protocol", propertyValue = When.NULL)
    private IMAPConfiguration imapConfiguration;

    private IMAPListenerThread listenerThread;
    private IMAPStore store;
    private Folder folder;

    @Override
    public void onStart() {
        String username = imapConfiguration.getUsername(); // or throw
        String password = imapConfiguration.getPassword(); // or throw
        String folderName = imapConfiguration.getFolder(); // or default (INBOX)

        Session session = Session.getInstance(new IMAPProperties(imapConfiguration));
        try {
            store = (IMAPStore) session.getStore();
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

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public POP3Configuration getPop3Configuration() {
        return pop3Configuration;
    }

    public void setPop3Configuration(POP3Configuration pop3Configuration) {
        this.pop3Configuration = pop3Configuration;
    }

    public IMAPConfiguration getImapConfiguration() {
        return imapConfiguration;
    }

    public void setImapConfiguration(IMAPConfiguration imapConfiguration) {
        this.imapConfiguration = imapConfiguration;
    }
}
