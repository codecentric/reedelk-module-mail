package com.reedelk.mail.component;

import com.reedelk.mail.internal.SchedulerProvider;
import com.reedelk.mail.internal.listener.pop3.POP3PollingStrategy;
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

    @Property("Delete after retrieve") // TODO: HERE THE POP3 REALLY DELETES the MESSAGES!
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    private Boolean deleteOnSuccess;

    @Property("Batch Emails")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true emails are batched in a list")
    private Boolean batchEmails;

    private SchedulerProvider schedulerProvider;

    @Override
    public void onStart() {
        requireNotNull(POP3MailListener.class, configuration, "POP3 Configuration");
        requireNotNull(POP3MailListener.class, configuration.getHost(), "POP3 hostname must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getUsername(), "POP3 username must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getPassword(), "POP3 password must not be empty.");

        POP3PollingStrategy pollingStrategy = new POP3PollingStrategy(this, configuration, deleteOnSuccess, batchEmails);
        this.schedulerProvider = new SchedulerProvider();
        this.schedulerProvider.schedule(pollInterval, pollingStrategy);
    }

    @Override
    public void onShutdown() {
        if (schedulerProvider != null) schedulerProvider.stop();
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

    public Boolean getDeleteOnSuccess() {
        return deleteOnSuccess;
    }

    public void setDeleteOnSuccess(Boolean deleteOnSuccess) {
        this.deleteOnSuccess = deleteOnSuccess;
    }

    public Boolean getBatchEmails() {
        return batchEmails;
    }

    public void setBatchEmails(Boolean batchEmails) {
        this.batchEmails = batchEmails;
    }
}
