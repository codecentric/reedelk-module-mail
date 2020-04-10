package com.reedelk.mail.component;

import com.reedelk.mail.internal.SchedulerProvider;
import com.reedelk.mail.internal.listener.imap.IMAPIdleListener;
import com.reedelk.mail.internal.listener.imap.IMAPPollingStrategy;
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

    @Property("Batch messages")
    @DefaultValue("false")
    @Example("true")
    @Group("General")
    @Description("If true emails are batched in a list")
    private Boolean batch;

    @Property("Delete on success")
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
    @Hint("10")
    @Example("10")
    @Group("Listening Strategy")
    @Description("Limits the number of emails to be processed.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer limit;

    @Property("Poll Flags")
    @Group("Listening Strategy")
    @Description("Flags to be used to fetch messages when strategy is 'POLLING'.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private IMAPFlags flags;

    private IMAPIdleListener idle;
    private SchedulerProvider schedulerProvider;
    private IMAPPollingStrategy pollingStrategy;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "IMAP Configuration");
        requireNotNull(IMAPMailListener.class, configuration.getHost(), "IMAP hostname must not be empty.");
        requireNotNull(IMAPMailListener.class, configuration.getUsername(), "IMAP username must not be empty.");
        requireNotNull(IMAPMailListener.class, configuration.getPassword(), "IMAP password must not be empty.");

        if (IMAPListeningStrategy.POLLING.equals(strategy)) {
            pollingStrategy = new IMAPPollingStrategy(this, configuration, flags, folder, deleteOnSuccess, batch, peek, limit);
            schedulerProvider = new SchedulerProvider();
            schedulerProvider.schedule(pollInterval, pollingStrategy);
        } else {
            // IDLE
            idle = new IMAPIdleListener(this, configuration, folder, peek, deleteOnSuccess, batch);
            idle.start();
        }
    }

    @Override
    public void onShutdown() {
        if (pollingStrategy != null) {
            pollingStrategy.stop();
        }
        if (schedulerProvider != null) {
            schedulerProvider.stop();
        }
        if (idle != null) {
            idle.stop();
        }
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
}
