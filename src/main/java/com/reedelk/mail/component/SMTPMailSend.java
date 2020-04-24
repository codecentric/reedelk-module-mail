package com.reedelk.mail.component;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.mail.component.smtp.BodyDefinition;
import com.reedelk.mail.internal.commons.MailMessageToMessageAttributesMapper;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import com.reedelk.mail.internal.smtp.type.MailTypeFactory;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.MAIL_MESSAGE_ERROR;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static com.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNullOrBlank;

@ModuleComponent("Mail Send (SMTP)")
@Description("Sends an email using SMTP or SMPTs. " +
        "Several attachments can be configured to be sent together in the email. " +
        "Attachments might come from different sources such as " +
        "filesystem files, project resources or evaluated script expressions.")
@Component(service = SMTPMailSend.class, scope = ServiceScope.PROTOTYPE)
public class SMTPMailSend implements ProcessorSync {

    @Property("SMTP Connection")
    private SMTPConfiguration configuration;

    @Property("From address")
    @Hint("from@domain.com")
    @Description("Sets the source address to be used in the email. " +
            "It can be a static or a dynamic expression.")
    @Example("<ul>" +
            "<li>Static string: from@domain.com</li>" +
            "<li>Config property: ${my.source.email.config.property}</li>" +
            "</ul>")
    private DynamicString from;

    @Property("To addresses")
    @Hint("toAddress1@domain.com,toAddress2@domain.com,toAddress3@domain.com")
    @Description("Sets the destination addresses of the email. " +
            "It can contain a comma separated list of recipients.")
    @Example("<ul>" +
            "<li>To string: toAddress1@domain.com,toAddress2@domain.com</li>" +
            "<li>To joined from list: <code>['toAddress1@domain.com','toAddress2@domain.com'].join(',')</code></li>" +
            "</ul>")
    private DynamicString to;

    @Property("Subject")
    @Hint("My email subject")
    @Example("An important subject")
    @Description("Sets the subject to be used in the email.")
    private DynamicString subject;

    @Property("Content")
    @Group("Body")
    private BodyDefinition body;

    @Property("Cc addresses")
    @Group("Recipients")
    @Hint("cc1@domain.com,cc2@domain.com,cc3@domain.com")
    @Description("The 'CC' addresses to be used in the email. " +
            "It can contain a comma separated list of addresses.")
    @Example("<ul>" +
            "<li>Cc string: cc1@domain.com,cc2@domain.com</li>" +
            "<li>Cc joined from list: <code>['cc1@domain.com','cc2@domain.com'].join(',')</code></li>" +
            "</ul>")
    private DynamicString cc;

    @Property("Bcc addresses")
    @Group("Recipients")
    @Hint("bcc1@domain.com,bcc2@domain.com,bcc3@domain.com")
    @Description("The 'BCC' addresses to be used in the email. " +
            "It can contain a comma separated list of addresses.")
    @Example("<ul>" +
            "<li>Bcc string: bcc1@domain.com,bcc2@domain.com</li>" +
            "<li>Bcc joined from list: <code>['bcc1@domain.com','bcc2@domain.com'].join(',')</code></li>" +
            "</ul>")
    private DynamicString bcc;

    @Property("Reply To addresses")
    @Group("Recipients")
    @Hint("replyTo1@domain.com,replyTo2@domain.com,replyTo3@domain.com")
    @Description("The 'Reply To' addresses to be used in the email. " +
            "It can contain a comma separated list of reply to addresses.")
    private DynamicString replyTo;

    @Property("Attachments Map")
    @InitValue("#[]")
    @Example("<code>context.emailAttachments</code>")
    @Group("Attachments")
    @Description("Sets the attachments map to be used as a source of the mail attachments. " +
            "An attachments map can be created using: " +
            "<code>" +
            "{'attachment1': MailAttachmentBuilder.create().text('hello world').filename('my-text.txt').build() }" +
            "</code>")
    private DynamicObject attachmentsMap;

    @Property("Attachments")
    @Group("Attachments")
    @TabGroup("Attachments List")
    @ListDisplayProperty("name")
    @DialogTitle("Attachment Configuration")
    private List<AttachmentDefinition> attachments = new ArrayList<>();

    @Reference
    ScriptEngineService scriptService;
    @Reference
    ConverterService converterService;

    @Override
    public void initialize() {
        requireNotNull(SMTPMailSend.class, configuration, "SMTP Configuration is not defined.");
        requireNotNull(SMTPMailSend.class, body, "Email body is not defined.");
        requireNotNullOrBlank(SMTPMailSend.class, from, "'From' must not be blank");
        requireNotNullOrBlank(SMTPMailSend.class, to, "'To' must not be blank");
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        try {

            Email email = MailTypeFactory.from(this).create(flowContext, message);

            email.send();

            Map<String, Serializable> attributes = MailMessageToMessageAttributesMapper.from(email);

            return MessageBuilder.get(SMTPMailSend.class)
                    .attributes(attributes)
                    .empty()
                    .build();

        } catch (EmailException exception) {
            throw new MailMessageConfigurationException(MAIL_MESSAGE_ERROR.format(exception.getMessage()), exception);
        }
    }

    public SMTPConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SMTPConfiguration configuration) {
        this.configuration = configuration;
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

    public BodyDefinition getBody() {
        return body;
    }

    public void setBody(BodyDefinition body) {
        this.body = body;
    }

    public DynamicObject getAttachmentsMap() {
        return attachmentsMap;
    }

    public void setAttachmentsMap(DynamicObject attachmentsMap) {
        this.attachmentsMap = attachmentsMap;
    }

    public List<AttachmentDefinition> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDefinition> attachments) {
        this.attachments = attachments;
    }

    public ScriptEngineService getScriptService() {
        return scriptService;
    }

    public ConverterService getConverterService() {
        return converterService;
    }
}
