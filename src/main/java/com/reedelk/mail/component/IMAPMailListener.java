package com.reedelk.mail.component;

import com.reedelk.mail.component.imap.IMAPFlags;
import com.reedelk.mail.component.imap.IMAPListeningStrategy;
import com.reedelk.mail.internal.MailPoller;
import com.reedelk.mail.internal.imap.IMAPIdleListener;
import com.reedelk.mail.internal.imap.IMAPIdleListenerSettings;
import com.reedelk.mail.internal.imap.IMAPPollingStrategy;
import com.reedelk.mail.internal.imap.IMAPPollingStrategySettings;
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
    private IMAPConfiguration configuration;

    @Property("IMAP Folder")
    @Example("INBOX")
    @Hint("INBOX")
    @InitValue("INBOX")
    @DefaultValue("INBOX")
    @Description("The IMAP folder from which the listener should be listening from.")
    private String folder;

    @Property("Peek")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true sets the message as 'seen' in the IMAP folder when the processing was successful.")
    private Boolean peek;

    @Property("Batch Messages")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true emails are batched in a list")
    private Boolean batch;

    @Property("Delete On success")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true deletes completely a message from the mailbox. If you only want to mark a message as 'deleted' use the property below.")
    private Boolean deleteOnSuccess;

    @Property("Strategy")
    @Example("IDLE")
    @DefaultValue("POLLING")
    @Group("Listening Strategy")
    private IMAPListeningStrategy strategy;

    @Property("Poll Interval")
    @Hint("120000")
    @Example("120000")
    @DefaultValue("60000")
    @Group("Listening Strategy")
    @Description("Poll interval delay. New messages will be checked every T + 'poll interval' time. Can be applied only when strategy is 'POLLING'.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer pollInterval;

    @Property("Limit")
    @Hint("25")
    @Example("25")
    @DefaultValue("10")
    @Group("Listening Strategy")
    @Description("Limits the number of emails to be processed for each poll.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer limit;

    @Property("Mark message/s deleted on success")
    @DefaultValue("false")
    @Example("true")
    @Group("Listening Strategy")
    @Description("If true deletes completely a message from the mailbox. If you only want to mark a message as 'deleted' use the property below.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Boolean markDeleteOnSuccess;

    @Property("Poll Flags")
    @Group("Listening Strategy")
    @Description("Flags to be used to fetch messages when strategy is 'POLLING'.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private IMAPFlags flags;

    private IMAPIdleListener idle;
    private MailPoller mailPoller;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "IMAP Configuration");
        requireNotNull(IMAPMailListener.class, configuration.getHost(), "IMAP hostname must not be empty.");
        requireNotNull(IMAPMailListener.class, configuration.getUsername(), "IMAP username must not be empty.");
        requireNotNull(IMAPMailListener.class, configuration.getPassword(), "IMAP password must not be empty.");

        if (IMAPListeningStrategy.POLLING.equals(strategy)) {
            IMAPPollingStrategySettings settings = IMAPPollingStrategySettings.create()
                    .markDeleteOnSuccess(markDeleteOnSuccess)
                    .deleteOnSuccess(deleteOnSuccess)
                    .configuration(configuration)
                    .matcher(flags)
                    .folder(folder)
                    .batch(batch)
                    .limit(limit)
                    .peek(peek)
                    .build();

            IMAPPollingStrategy pollingStrategy = new IMAPPollingStrategy(this, settings);
            mailPoller = new MailPoller();
            mailPoller.schedule(pollInterval, pollingStrategy);

        } else {
            // IDLE Command
            IMAPIdleListenerSettings settings = IMAPIdleListenerSettings.create()
                    .deleteOnSuccess(deleteOnSuccess)
                    .configuration(configuration)
                    .folder(folder)
                    .batch(batch)
                    .peek(peek)
                    .build();
            idle = new IMAPIdleListener(this, settings);
            idle.start();
        }
    }

    @Override
    public void onShutdown() {
        if (mailPoller != null) mailPoller.stop();
        if (idle != null) idle.stop();
    }

    public void setConfiguration(IMAPConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setPeek(Boolean peek) {
        this.peek = peek;
    }

    public void setBatch(Boolean batch) {
        this.batch = batch;
    }

    public void setDeleteOnSuccess(Boolean deleteOnSuccess) {
        this.deleteOnSuccess = deleteOnSuccess;
    }

    public void setStrategy(IMAPListeningStrategy strategy) {
        this.strategy = strategy;
    }

    public void setPollInterval(Integer pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setFlags(IMAPFlags flags) {
        this.flags = flags;
    }

    public void setMarkDeleteOnSuccess(Boolean markDeleteOnSuccess) {
        this.markDeleteOnSuccess = markDeleteOnSuccess;
    }
}
