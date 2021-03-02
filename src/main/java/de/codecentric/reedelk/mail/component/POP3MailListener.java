package de.codecentric.reedelk.mail.component;

import de.codecentric.reedelk.mail.internal.CloseableService;
import de.codecentric.reedelk.mail.internal.MailPoller;
import de.codecentric.reedelk.mail.internal.PollingStrategy;
import de.codecentric.reedelk.mail.internal.pop3.POP3PollingStrategy;
import de.codecentric.reedelk.mail.internal.pop3.POP3PollingStrategySettings;
import de.codecentric.reedelk.mail.internal.type.ListOfMailMessage;
import de.codecentric.reedelk.mail.internal.type.MailMessage;
import de.codecentric.reedelk.runtime.api.annotation.*;
import de.codecentric.reedelk.runtime.api.component.AbstractInbound;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static de.codecentric.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener (POP3)")
@ComponentOutput(
        attributes = MessageAttributes.class,
        payload = { MailMessage.class, ListOfMailMessage.class } )
@Description("The Mail Listener connector provides a listener that polls for changes from a remote POP3 mailbox. " +
        "Every time a new email is received, a new event is triggered and the flow following this component is executed.")
@Component(service = POP3MailListener.class, scope = PROTOTYPE)
public class POP3MailListener extends AbstractInbound {

    @DialogTitle("POP3 Connection")
    @Property("POP3 Connection")
    private POP3Configuration connection;

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
        requireNotNull(POP3MailListener.class, connection, "POP3 Configuration is not defined.");
        requireNotNull(POP3MailListener.class, connection.getHost(), "POP3 hostname must not be empty.");
        requireNotNull(POP3MailListener.class, connection.getUsername(), "POP3 username must not be empty.");
        requireNotNull(POP3MailListener.class, connection.getPassword(), "POP3 password must not be empty.");

        POP3PollingStrategySettings settings = POP3PollingStrategySettings.create()
                .deleteOnSuccess(deleteOnSuccess)
                .configuration(connection)
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

    public POP3Configuration getConnection() {
        return connection;
    }

    public void setConnection(POP3Configuration connection) {
        this.connection = connection;
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
