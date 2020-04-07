package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

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

    @Override
    public void onStart() {

    }

    @Override
    public void onShutdown() {

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
