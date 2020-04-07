package com.reedelk.mail.component;

import com.reedelk.mail.internal.commons.MailMessageToMessageAttributesMapper;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import com.reedelk.mail.internal.send.type.MailTypeFactory;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.MAIL_MESSAGE_ERROR;
import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNullOrBlank;


@ModuleComponent("Mail Send")
@Description("Send email using POP3 or IMAP.")
@Component(service = MailSend.class, scope = ServiceScope.PROTOTYPE)
public class MailSend implements ProcessorSync {

    @Property("SMTP Connection")
    @Group("General")
    private SMTPConfiguration connectionConfiguration;

    @Property("From address")
    @Group("General")
    @Hint("from@domain.com")
    @Description("Sets the source address to be used in the email. " +
            "It can be a static or a dynamic expression.")
    @Example("<ul>" +
            "<li>Static string: from@domain.com</li>" +
            "<li>Config property: ${my.source.email.config.property}</li>" +
            "</ul>")
    private DynamicString from;

    @Property("To addresses")
    @Group("General")
    @Hint("toAddress1@domain.com,toAddress2@domain.com,toAddress3@domain.com")
    @Description("Sets the destination addresses of the email. " +
            "It can contain a comma separated list of recipients.")
    @Example("<ul>" +
            "<li>To string: toAddress1@domain.com,toAddress2@domain.com</li>" +
            "<li>To joined from list: <code>['toAddress1@domain.com','toAddress2@domain.com'].join(',')</code></li>" +
            "</ul>")
    private DynamicString to;

    @Property("Subject")
    @Group("General")
    @Example("An important subject")
    @Hint("My email subject")
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
        requireNotNullOrBlank(MailSend.class, to, "To must not be blank");
        requireNotNullOrBlank(MailSend.class, from, "From must not be blank");
        requireNotNull(MailSend.class, body, "Email body definition must be defined");
        requireNotNull(MailSend.class, connectionConfiguration, "Connection configuration is mandatory");
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        try {

            Email email = MailTypeFactory.from(this).create(flowContext, message);

            email.send();

            MessageAttributes attributes =
                    MailMessageToMessageAttributesMapper.from(MailSend.class, email);

            return MessageBuilder.get()
                    .attributes(attributes)
                    .empty()
                    .build();

        } catch (EmailException | MessagingException exception) {
            throw new MailMessageConfigurationException(MAIL_MESSAGE_ERROR.format(exception.getMessage()), exception);
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
