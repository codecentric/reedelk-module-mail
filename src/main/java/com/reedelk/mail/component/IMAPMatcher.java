package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Collapsible
@Component(service = IMAPMatcher.class, scope = ServiceScope.PROTOTYPE)
public class IMAPMatcher implements Implementor {

    @Property("Seen Messages")
    @Example("BOTH")
    @DefaultValue("NO")
    @Description("If true, seen email messages are fetched from the IMAP server.")
    private IMAPMatcherFlag seen = IMAPMatcherFlag.NO;

    @Property("Recent Messages")
    @Example("YES")
    @DefaultValue("NO")
    @Description("If true, email messages marked as 'recent' are fetched from the IMAP server.")
    private IMAPMatcherFlag recent = IMAPMatcherFlag.NO;

    @Property("Deleted Messages")
    @Example("YES")
    @DefaultValue("NO")
    @Description("If true, email messages marked as 'deleted' are fetched from the IMAP server.")
    private IMAPMatcherFlag deleted = IMAPMatcherFlag.NO;

    @Property("Answered Messages")
    @Example("BOTH")
    @DefaultValue("NO")
    @Description("If true, answered email messages are fetched from the IMAP server.")
    private IMAPMatcherFlag answered = IMAPMatcherFlag.NO;

    public IMAPMatcherFlag getSeen() {
        return seen;
    }

    public void setSeen(IMAPMatcherFlag seen) {
        this.seen = seen;
    }

    public IMAPMatcherFlag getRecent() {
        return recent;
    }

    public void setRecent(IMAPMatcherFlag recent) {
        this.recent = recent;
    }

    public IMAPMatcherFlag getDeleted() {
        return deleted;
    }

    public void setDeleted(IMAPMatcherFlag deleted) {
        this.deleted = deleted;
    }

    public IMAPMatcherFlag getAnswered() {
        return answered;
    }

    public void setAnswered(IMAPMatcherFlag answered) {
        this.answered = answered;
    }
}
