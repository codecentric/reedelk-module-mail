package com.reedelk.mail.component;

import com.reedelk.mail.internal.CloseableService;
import com.reedelk.mail.internal.MailPoller;
import com.reedelk.mail.internal.PollingStrategy;
import com.reedelk.mail.internal.pop3.POP3PollingStrategy;
import com.reedelk.mail.internal.pop3.POP3PollingStrategySettings;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener (POP3)")
@Description("The Mail Listener connector provides a listener that polls for changes from a remote POP3 mailbox. " +
        "Every time a new email is received, a new event is triggered and the flow following this component is executed.")
@Component(service = POP3MailListener.class, scope = PROTOTYPE)
public class POP3MailListener extends AbstractInbound {

    @Property("POP3 Connection")
    private POP3Configuration configuration;

    @Property("Poll Interval")
    @Hint("120000")
    @Example("120000")
    @DefaultValue("60000")
    @Description("Sets the poll interval delay. " +
            "New messages will be checked every X + 'poll interval' delay time.")
    private Integer pollInterval;

    @Property("Limit")
    @Hint("10")
    @Example("25")
    @DefaultValue("10")
    @Description("Limits the number of emails to be processed for each poll. " +
            "If the number of emails fetched during a poll is greater than the limit, " +
            "the remaining emails will be processed in the next poll iteration.")
    private Integer limit;

    @Property("Delete on success")
    @Example("true")
    @DefaultValue("false")
    @Description("If true deletes permanently a message from the POP3 mailbox whenever the flow completes successfully.")
    private Boolean deleteOnSuccess;

    @Property("Batch messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true and there are multiple emails in the IMAP folder, emails are grouped in a list. " +
            "The message following the listener is a list containing email data inside a map object.")
    private Boolean batch;

    @Reference
    CloseableService closeableService;

    @Override
    public void onStart() {
        requireNotNull(POP3MailListener.class, configuration, "POP3 Configuration is not defined.");
        requireNotNull(POP3MailListener.class, configuration.getHost(), "POP3 hostname must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getUsername(), "POP3 username must not be empty.");
        requireNotNull(POP3MailListener.class, configuration.getPassword(), "POP3 password must not be empty.");

        POP3PollingStrategySettings settings = POP3PollingStrategySettings.create()
                .deleteOnSuccess(deleteOnSuccess)
                .configuration(configuration)
                .batch(batch)
                .limit(limit)
                .build();
        PollingStrategy pollingStrategy = new POP3PollingStrategy(this, settings);
        MailPoller mailPoller = new MailPoller();
        mailPoller.schedule(pollInterval, pollingStrategy);
        closeableService.register(this, mailPoller);
    }

    @Override
    public void onShutdown() {
        closeableService.unregister(this);
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
