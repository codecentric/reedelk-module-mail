package com.reedelk.mail.component.imap;

import com.reedelk.runtime.api.annotation.DefaultValue;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.Example;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.Implementor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = IMAPFlags.class, scope = ServiceScope.PROTOTYPE)
public class IMAPFlags implements Implementor {

    @Property("Seen Messages")
    @Example("BOTH")
    @DefaultValue("NO")
    @Description("If true, seen email messages are fetched from the IMAP server.")
    private IMAPFlag seen = IMAPFlag.NO;

    @Property("Deleted Messages")
    @Example("YES")
    @DefaultValue("NO")
    @Description("If true, email messages marked as 'deleted' are fetched from the IMAP server.")
    private IMAPFlag deleted = IMAPFlag.NO;

    @Property("Answered Messages")
    @Example("BOTH")
    @DefaultValue("NO")
    @Description("If true, answered email messages are fetched from the IMAP server.")
    private IMAPFlag answered = IMAPFlag.NO;

    public IMAPFlag getSeen() {
        return seen;
    }

    public void setSeen(IMAPFlag seen) {
        this.seen = seen;
    }

    public IMAPFlag getDeleted() {
        return deleted;
    }

    public void setDeleted(IMAPFlag deleted) {
        this.deleted = deleted;
    }

    public IMAPFlag getAnswered() {
        return answered;
    }

    public void setAnswered(IMAPFlag answered) {
        this.answered = answered;
    }
}
