package com.reedelk.mail.component;

import com.reedelk.mail.internal.send.MailMessageBuilder;
import com.reedelk.mail.internal.send.SMTPSessionBuilder;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.mail.Session;
import javax.mail.Transport;

import java.util.List;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNullOrBlank;


@ModuleComponent("Mail Send")
@Description("Send email using POP3 or IMAP.")
@Component(service = MailSend.class, scope = ServiceScope.PROTOTYPE)
public class MailSend implements ProcessorSync {

    @Property("Connection")
    @Group("General")
    private SMTPConfiguration connectionConfiguration;

    @Property("From address")
    @Hint("from@domain.com")
    @Group("General")
    @Description("The From address to be used in the email")
    private DynamicString from;

    @Property("To addresses")
    @Hint("dest1@domain.com,dest2@domain.com,dest3@domain.com")
    @Group("General")
    private DynamicString to;

    @Property("Subject")
    @Hint("My email subject")
    @Group("General")
    private DynamicString subject;

    @Property("Content")
    @Group("Body")
    private BodyConfiguration body;

    @Property("Cc addresses")
    @Hint("cc1@my-domain.com,cc2@my-domain.com")
    @Group("Recipients")
    private DynamicString cc;

    @Property("Bcc addresses")
    @Hint("bcc1@my-domain.com,bcc2@my-domain.com")
    @Group("Recipients")
    private DynamicString bcc;

    @Property("Reply to addresses")
    @Hint("replyTo1@my-domain.com,replyTo2@my-domain.com")
    @Group("Recipients")
    private DynamicString replyTo;

    @Property("Attachments Object")
    @Group("Attachments")
    private DynamicObject attachmentsObject;

    @Property("Attachments")
    @Group("Attachments")
    @TabGroup("Attachments")
    @ListDisplayProperty("name")
    private List<AttachmentDefinition> attachments;

    @Reference
    private ScriptEngineService scriptService;

    private Session session;

    @Override
    public void initialize() {
        requireNotNullOrBlank(MailSend.class, to, "To must not be blank");
        requireNotNullOrBlank(MailSend.class, from, "From must not be blank");
        requireNotNull(MailSend.class, connectionConfiguration, "Connection configuration is mandatory");
        this.session = SMTPSessionBuilder.builder()
                .configuration(connectionConfiguration)
                .build();
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        try {

            javax.mail.Message mailMessage = MailMessageBuilder.builder()
                    .cc(cc)
                    .to(to)
                    .bcc(bcc)
                    .body(body)
                    .from(from)
                    .session(session)
                    .replyTo(replyTo)
                    .subject(subject)
                    .message(message)
                    .context(flowContext)
                    .scriptService(scriptService)
                    .build();

            Transport.send(mailMessage);

            return MessageBuilder.get().empty().build();

        } catch (Exception e) {
            throw new ESBException(e);
        }
    }

    public SMTPConfiguration getConnectionConfiguration() {
        return connectionConfiguration;
    }

    public void setConnectionConfiguration(SMTPConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    public DynamicString getFrom() {
        return from;
    }

    public void setFrom(DynamicString from) {
        this.from = from;
    }

    public DynamicString getTo() {
        return to;
    }

    public void setTo(DynamicString to) {
        this.to = to;
    }

    public DynamicString getSubject() {
        return subject;
    }

    public void setSubject(DynamicString subject) {
        this.subject = subject;
    }

    public DynamicString getCc() {
        return cc;
    }

    public void setCc(DynamicString cc) {
        this.cc = cc;
    }

    public DynamicString getBcc() {
        return bcc;
    }

    public void setBcc(DynamicString bcc) {
        this.bcc = bcc;
    }

    public DynamicString getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(DynamicString replyTo) {
        this.replyTo = replyTo;
    }

    public BodyConfiguration getBody() {
        return body;
    }

    public void setBody(BodyConfiguration body) {
        this.body = body;
    }

    public DynamicObject getAttachmentsObject() {
        return attachmentsObject;
    }

    public void setAttachmentsObject(DynamicObject attachmentsObject) {
        this.attachmentsObject = attachmentsObject;
    }

    public List<AttachmentDefinition> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDefinition> attachments) {
        this.attachments = attachments;
    }
}
