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
    private IMAPListeningStrategy strategy;

    @Property("Matcher")
    @Group("Fetch Conditions")
    private IMAPMatcher matcher;

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

    private ProtocolMailListener mailListener;

    @Override
    public void onStart() {
        requireNotNull(IMAPMailListener.class, configuration, "IMAP Configuration");
        if (IMAPListeningStrategy.IDLE.equals(strategy)) {
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
