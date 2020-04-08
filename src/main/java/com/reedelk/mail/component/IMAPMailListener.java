package com.reedelk.mail.component;

import com.reedelk.mail.internal.SchedulerProvider;
import com.reedelk.mail.internal.listener.imap.ImapIdleMailListener;
import com.reedelk.mail.internal.listener.imap.ImapPollingStrategy;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.concurrent.ScheduledFuture;

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
    private IMAPListeningStrategy strategy;

    @Property("Matcher")
    @Group("Fetch Conditions")
    private IMAPMatcher matcher;

    @Property("Poll Interval")
    @Group("General")
    @Hint("120000")
    @Example("120000")
    @DefaultValue("60000")
    @Description("Poll interval delay. New messages will be checked every T + 'poll interval' time.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer pollInterval;

    @Property("Mark as deleted if success")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    private Boolean deleteOnSuccess;

    @Property("Mark as seen if success")
    @DefaultValue("true")
    @Example("true")
    @Group("General")
    private Boolean seenOnSuccess;

    @Property("Batch Emails")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true emails are batched in a list")
    private Boolean batchEmails;

    @Reference
    private SchedulerProvider schedulerProvider;

    private ScheduledFuture<?> scheduled;
    private ImapIdleMailListener idle;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "IMAP Configuration");

        if (IMAPListeningStrategy.POLLING.equals(strategy)) {
            ImapPollingStrategy pollingStrategy = new ImapPollingStrategy(configuration, this);
            this.scheduled = schedulerProvider.schedule(pollInterval, pollingStrategy);
        } else {
            // IDLE
            idle = new ImapIdleMailListener(configuration, this);
            idle.start();
        }
    }

    @Override
    public void onShutdown() {
        schedulerProvider.cancel(scheduled);
        if (idle != null) idle.stop();
    }

    public IMAPConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IMAPConfiguration configuration) {
        this.configuration = configuration;
    }

    public IMAPListeningStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IMAPListeningStrategy strategy) {
        this.strategy = strategy;
    }

    public IMAPMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(IMAPMatcher matcher) {
        this.matcher = matcher;
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

    public Boolean getSeenOnSuccess() {
        return seenOnSuccess;
    }

    public void setSeenOnSuccess(Boolean seenOnSuccess) {
        this.seenOnSuccess = seenOnSuccess;
    }

    public Boolean getBatchEmails() {
        return batchEmails;
    }

    public void setBatchEmails(Boolean batchEmails) {
        this.batchEmails = batchEmails;
    }
}
