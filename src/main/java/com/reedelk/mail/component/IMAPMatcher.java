package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Collapsible
@Component(service = IMAPMatcher.class, scope = ServiceScope.PROTOTYPE)
public class IMAPMatcher implements Implementor {

    @Property("Fetch Seen Messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true, seen email messages are fetched from the IMAP server.")
    private Boolean seen;

    @Property("Fetch Recent Messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true, email messages marked as 'recent' are fetched from the IMAP server.")
    private Boolean recent;

    @Property("Fetch Deleted Messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true, email messages marked as 'deleted' are fetched from the IMAP server.")
    private Boolean deleted;

    @Property("Fetch Answered Messages")
    @Example("true")
    @DefaultValue("false")
    @Description("If true, answered email messages are fetched from the IMAP server.")
    private Boolean answered;

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getRecent() {
        return recent;
    }

    public void setRecent(Boolean recent) {
        this.recent = recent;
    }
}
