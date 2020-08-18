package com.reedelk.mail.component;

import com.reedelk.mail.component.imap.IMAPFlags;
import com.reedelk.mail.component.imap.IMAPListeningStrategy;
import com.reedelk.mail.internal.CloseableService;
import com.reedelk.mail.internal.MailPoller;
import com.reedelk.mail.internal.imap.IMAPIdleListener;
import com.reedelk.mail.internal.imap.IMAPIdleListenerSettings;
import com.reedelk.mail.internal.imap.IMAPPollingStrategy;
import com.reedelk.mail.internal.imap.IMAPPollingStrategySettings;
import com.reedelk.mail.internal.type.ListOfMailMessage;
import com.reedelk.mail.internal.type.MailMessage;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Optional;

import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener (IMAP)")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = { MailMessage.class, ListOfMailMessage.class } )
@Description("The Mail Listener connector provides a listener that listens for changes from a remote IMAP mailbox. " +
        "Every time a new email is received, a new event is triggered and the flow following this component is executed. " +
        "For IMAP type mailboxes there are two poll strategies: POLLING and IDLE. " +
        "The polling strategy periodically checks for new emails on the remote server. The IDLE strategy allows the " +
        "component to receive in real time new emails events from the remote host, without requiring polling. " +
        "This capability depends on the server and it is not always supported.")
@Component(service = IMAPMailListener.class, scope = PROTOTYPE)
public class IMAPMailListener extends AbstractInbound {

    @DialogTitle("IMAP Connection")
    @Property("IMAP Connection")
    private IMAPConfiguration connection;

    @Property("IMAP Folder")
    @Hint("INBOX")
    @Example("INBOX")
    @DefaultValue("INBOX")
    @Description("The name of the IMAP folder this listener should be checking email from.")
    private String folder;

    @Property("Peek")
    @Example("true")
    @DefaultValue("false")
    @Description("If true the mail message is not set as 'seen' in the IMAP folder it was pulled from.")
    private Boolean peek;

    @Property("Batch Messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true and there are multiple emails in the IMAP folder, emails are grouped in a list. " +
            "The message following the listener is a list containing email data inside a map object.")
    private Boolean batch;

    @Property("Delete On success")
    @Example("true")
    @DefaultValue("false")
    @Description("If true deletes permanently a message from the IMAP folder whenever the flow completes successfully. " +
            "If you only want to mark a message 'deleted' without removing it permanently use the property named 'Mark message/s deleted on success'.")
    private Boolean deleteOnSuccess;

    @Property("Strategy")
    @Example("IDLE")
    @DefaultValue("POLLING")
    @Group("Listening Strategy")
    @Description("The polling strategy used by the listener: it could be <i>POLLING</i> or <i>IDLE</i>. " +
            "The polling strategy periodically checks for new emails on the remote server. The IDLE strategy allows the " +
            "component to receive in real time new emails events from the remote host, without requiring polling. " +
            "This capability depends on the server and it is not always supported.")
    private IMAPListeningStrategy strategy;

    @Property("Poll Interval")
    @Hint("120000")
    @Example("120000")
    @DefaultValue("60000")
    @Group("Listening Strategy")
    @Description("Sets the poll interval delay. " +
            "New messages will be checked every X + 'poll interval' delay time. " +
            "Can be applied only when strategy is 'POLLING'.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer pollInterval;

    @Property("Limit")
    @Hint("10")
    @Example("25")
    @DefaultValue("10")
    @Group("Listening Strategy")
    @Description("Limits the number of emails to be processed for each poll. " +
            "If the number of emails fetched during a poll is greater than the limit, " +
            "the remaining emails will be processed in the next poll iteration.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Integer limit;

    @Property("Mark message/s deleted on success")
    @DefaultValue("false")
    @Example("true")
    @Group("Listening Strategy")
    @Description("If true marks a message to be 'deleted' without removing it permanently.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private Boolean markDeleteOnSuccess;

    @Property("Poll Flags")
    @Group("Listening Strategy")
    @Description("Flags to be used to fetch messages when strategy is 'POLLING'. " +
            "The flags can be used to fetch seen/deleted or answered messages.")
    @When(propertyName = "strategy", propertyValue = "POLLING")
    @When(propertyName = "strategy", propertyValue = When.NULL)
    private IMAPFlags flags;

    @Reference
    CloseableService closeableService;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, connection, "IMAP Configuration is not defined.");
        requireNotNull(IMAPMailListener.class, connection.getHost(), "IMAP hostname must not be empty.");
        requireNotNull(IMAPMailListener.class, connection.getUsername(), "IMAP username must not be empty.");
        requireNotNull(IMAPMailListener.class, connection.getPassword(), "IMAP password must not be empty.");

        // Default strategy is polling.
        IMAPListeningStrategy strategy =
                Optional.ofNullable(this.strategy)
                        .orElse(IMAPListeningStrategy.POLLING);

        if (IMAPListeningStrategy.POLLING.equals(strategy)) {
            IMAPPollingStrategySettings settings = IMAPPollingStrategySettings.create()
                    .markDeleteOnSuccess(markDeleteOnSuccess)
                    .deleteOnSuccess(deleteOnSuccess)
                    .configuration(connection)
                    .matcher(flags)
                    .folder(folder)
                    .batch(batch)
                    .limit(limit)
                    .peek(peek)
                    .build();

            IMAPPollingStrategy pollingStrategy = new IMAPPollingStrategy(this, settings);
            MailPoller poller = new MailPoller();
            poller.schedule(pollInterval, pollingStrategy);
            closeableService.register(this, poller);

        } else {
            // IDLE Command
            IMAPIdleListenerSettings settings = IMAPIdleListenerSettings.create()
                    .deleteOnSuccess(deleteOnSuccess)
                    .configuration(connection)
                    .folder(folder)
                    .batch(batch)
                    .peek(peek)
                    .build();
            IMAPIdleListener idle = new IMAPIdleListener(this, settings);
            idle.start();
            closeableService.register(this, idle);
        }
    }

    @Override
    public void onShutdown() {
        closeableService.unregister(this);
    }

    public void setConnection(IMAPConfiguration connection) {
        this.connection = connection;
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
