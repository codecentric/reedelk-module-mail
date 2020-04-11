package com.reedelk.mail.component;

import com.reedelk.mail.internal.MailPoller;
import com.reedelk.mail.internal.PollingStrategy;
import com.reedelk.mail.internal.pop3.POP3PollingStrategy;
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

    @Property("Limit")
    @Example("10")
    @Group("General")
    @Description("Limits the number of emails to be processed.")
    private Integer limit;

    @Property("Delete on success")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    private Boolean deleteOnSuccess;

    @Property("Batch messages")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true emails are batched in a list")
    private Boolean batch;

    private MailPoller mailPoller;

    @Override
    public void onStart() {
        requireNotNull(POP3MailListener.class, configuration, "POP3 Configuration");
        requireNotNull(POP3MailListener.class, configuration.getHost(), "POP3 hostname must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getUsername(), "POP3 username must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getPassword(), "POP3 password must not be empty.");

        PollingStrategy pollingStrategy = new POP3PollingStrategy(this, configuration, deleteOnSuccess, batch, limit);
        mailPoller = new MailPoller();
        mailPoller.schedule(pollInterval, pollingStrategy);
    }

    @Override
    public void onShutdown() {
        if (mailPoller != null) mailPoller.stop();
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

    public Boolean getBatch() {
        return batch;
    }

    public void setBatch(Boolean batch) {
        this.batch = batch;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
