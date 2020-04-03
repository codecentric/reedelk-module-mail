package com.reedelk.mail.component;

import com.reedelk.mail.internal.send.MailMessageBuilder;
import com.reedelk.mail.internal.send.SMTPSessionBuilder;
import com.reedelk.runtime.api.annotation.Description;
import com.reedelk.runtime.api.annotation.Group;
import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.mail.Session;
import javax.mail.Transport;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;


@ModuleComponent("Mail Send")
@Description("Send email using POP3 or IMAP.")
@Component(service = MailSend.class, scope = ServiceScope.PROTOTYPE)
public class MailSend implements ProcessorSync {

    @Property("Connection")
    @Group("General")
    private SMTPConfiguration connectionConfiguration;

    @Property("From address")
    @Group("General")
    private DynamicString from;

    @Property("To addresses")
    @Group("General")
    private DynamicString to;

    @Property("Subject")
    @Group("General")
    private DynamicString subject;

    @Property("Cc addresses")
    @Group("Advanced")
    private DynamicString cc;

    @Property("Bcc addresses")
    @Group("Advanced")
    private DynamicString bcc;

    @Property("Reply to addresses")
    @Group("Advanced")
    private DynamicString replyTo;

    @Property("Content")
    @Group("Body")
    private BodyConfiguration body;

    @Property("Attachments")
    @Group("Attachments")
    private AttachmentsConfiguration attachments;

    @Reference
    private ScriptEngineService scriptService;

    private Session session;

    @Override
    public void initialize() {
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

    public DynamicString getSubject() {
        return subject;
    }

    public void setSubject(DynamicString subject) {
        this.subject = subject;
    }

    public BodyConfiguration getBody() {
        return body;
    }

    public void setBody(BodyConfiguration body) {
        this.body = body;
    }

    public AttachmentsConfiguration getAttachments() {
        return attachments;
    }

    public void setAttachments(AttachmentsConfiguration attachments) {
        this.attachments = attachments;
    }

    public ScriptEngineService getScriptService() {
        return scriptService;
    }

    public void setScriptService(ScriptEngineService scriptService) {
        this.scriptService = scriptService;
    }

}
