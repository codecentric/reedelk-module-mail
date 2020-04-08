package com.reedelk.mail.component;

import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.mail.internal.listener.imap.ImapIdleMailListener;
import com.reedelk.mail.internal.listener.imap.ImapPollMailListener;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener (IMAP)")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = IMAPMailListener.class, scope = PROTOTYPE)
public class IMAPMailListener extends AbstractInbound {

    @Property("IMAP Connection")
    @Group("General")
    private IMAPConfiguration configuration;

    @Property("IMAP Strategy")
    @Example("IDLE")
    @DefaultValue("POLLING")
    private ImapListeningStrategy1 strategy;

    private ProtocolMailListener mailListener;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "IMAP Configuration");
        if (ImapListeningStrategy1.IDLE.equals(strategy)) {
            mailListener = new ImapIdleMailListener(configuration, this);
        } else {
            mailListener = new ImapPollMailListener(configuration, this);
        }
        mailListener.start();
    }

    @Override
    public void onShutdown() {
        if (mailListener != null) {
            mailListener.stop();
        }
    }

    public IMAPConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IMAPConfiguration configuration) {
        this.configuration = configuration;
    }

    public ImapListeningStrategy1 getStrategy() {
        return strategy;
    }

    public void setStrategy(ImapListeningStrategy1 strategy) {
        this.strategy = strategy;
    }
}
