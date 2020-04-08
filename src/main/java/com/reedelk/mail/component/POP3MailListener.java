package com.reedelk.mail.component;

import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.mail.internal.listener.pop3.POP3Listener;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.Group;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("POP3 Mail Listener")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = POP3MailListener.class, scope = PROTOTYPE)
public class POP3MailListener extends AbstractInbound {

    @Property("POP3 Connection")
    @Group("General")
    private POP3Configuration configuration;

    private ProtocolMailListener mailListener;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "POP3 Configuration");
        mailListener = new POP3Listener(configuration, this);
        mailListener.start();
    }

    @Override
    public void onShutdown() {
        if (mailListener != null) {
            mailListener.stop();
        }
    }
}
