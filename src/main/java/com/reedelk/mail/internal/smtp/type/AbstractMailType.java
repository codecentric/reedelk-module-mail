package com.reedelk.mail.internal.smtp.type;

import com.reedelk.mail.component.SMTPMailSend;
import com.reedelk.mail.component.smtp.BodyDefinition;
import com.reedelk.mail.internal.smtp.MailAttachmentBuilder;
import com.reedelk.mail.internal.smtp.MailMessageBuilder;
import com.reedelk.mail.internal.smtp.MailSessionBuilder;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.nio.charset.StandardCharsets;

import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

abstract class AbstractMailType implements MailTypeStrategy {

    protected final SMTPMailSend component;

    public AbstractMailType(SMTPMailSend component) {
        this.component = component;
    }

    protected void configureConnection(Email email) {
        MailSessionBuilder.builder(email)
                .configuration(component.getConnection())
                .build();
    }

    protected void configureBaseMessage(FlowContext context, Message message, Email email) throws EmailException {
        MailMessageBuilder.get(email)
                .scriptService(component.getScriptService())
                .replyTo(component.getReplyTo())
                .subject(component.getSubject())
                .from(component.getFrom())
                .bcc(component.getBcc())
                .to(component.getTo())
                .cc(component.getCc())
                .context(context)
                .message(message)
                .build();
    }


    protected void configureAttachments(FlowContext context, Message message, MultiPartEmail email) {
        MailAttachmentBuilder.get(email)
                .message(message)
                .context(context)
                .attachments(component.getAttachments())
                .converter(component.getConverterService())
                .scriptEngine(component.getScriptService())
                .attachmentsMap(component.getAttachmentsMap())
                .build();
    }

    protected String charsetFrom(BodyDefinition definition) {
        if (definition == null) return StandardCharsets.UTF_8.toString();
        if (isBlank(definition.getCharset())) return StandardCharsets.UTF_8.toString();
        else return definition.getCharset();
    }
}
