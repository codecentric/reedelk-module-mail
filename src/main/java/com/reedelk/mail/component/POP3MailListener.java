package com.reedelk.mail.component;

import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.mail.internal.listener.pop3.POP3Listener;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener (POP3)")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = POP3MailListener.class, scope = PROTOTYPE)
public class POP3MailListener extends AbstractInbound {

    @Property("POP3 Connection")
    @Group("General")
    private POP3Configuration configuration;

    @Property("Poll Interval")
    @Group("General")
    @Hint("120000")
    @Example("120000")
    @DefaultValue("60000")
    @Description("Poll interval delay. New messages will be checked every T + 'poll interval' time.")
    private Integer pollInterval;

    @Property("Delete after retrieve")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    private Boolean deleteAfterRetrieve;

    private ProtocolMailListener mailListener;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "POP3 Configuration");
        mailListener = new POP3Listener(configuration, pollInterval, deleteAfterRetrieve, this);
        mailListener.start();
    }

    @Override
    public void onShutdown() {
        if (mailListener != null) {
            mailListener.stop();
        }
    }

    public POP3Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(POP3Configuration configuration) {
        this.configuration = configuration;
    }

    public Integer getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(Integer pollInterval) {
        this.pollInterval = pollInterval;
    }

    public Boolean getDeleteAfterRetrieve() {
        return deleteAfterRetrieve;
    }

    public void setDeleteAfterRetrieve(Boolean deleteAfterRetrieve) {
        this.deleteAfterRetrieve = deleteAfterRetrieve;
    }
}
