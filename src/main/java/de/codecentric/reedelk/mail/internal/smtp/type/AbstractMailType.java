package de.codecentric.reedelk.mail.internal.smtp.type;

import de.codecentric.reedelk.mail.component.SMTPMailSend;
import de.codecentric.reedelk.mail.component.smtp.BodyDefinition;
import de.codecentric.reedelk.mail.internal.exception.MailMessageConfigurationException;
import de.codecentric.reedelk.mail.internal.smtp.MailAttachmentBuilder;
import de.codecentric.reedelk.mail.internal.smtp.MailMessageBuilder;
import de.codecentric.reedelk.mail.internal.smtp.MailSessionBuilder;
import de.codecentric.reedelk.runtime.api.commons.DynamicValueUtils;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.content.Pair;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import java.nio.charset.StandardCharsets;

import static de.codecentric.reedelk.mail.internal.commons.Messages.MailSendComponent.MAIL_BODY_EMPTY_ERROR;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;
import static java.util.Optional.ofNullable;

abstract class AbstractMailType implements MailTypeStrategy {

    protected final ConverterService converterService;
    protected final SMTPMailSend component;

    public AbstractMailType(SMTPMailSend component, ConverterService converterService) {
        this.component = component;
        this.converterService = converterService;
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

    protected Pair<String,String> buildCharsetAndMailBody(FlowContext context, Message message) {
        // Mail message body
        BodyDefinition body = component.getBody();

        String bodyContent;

        // If the user has not set the mail body content expression from the body definition,
        // we use the message payload as mail content. Note that the message payload
        // is converted to string first.
        DynamicString contentDynamicExpression = ofNullable(body)
                .flatMap(bodyDefinition -> ofNullable(bodyDefinition.getContent()))
                .orElse(null);

        if (DynamicValueUtils.isNullOrBlank(contentDynamicExpression)) {
            // We take the mail message body from the message payload.
            Object messageData = message.payload();
            bodyContent = converterService.convert(messageData, String.class);

        } else {
            // We evaluate the mail body content expression.
            ScriptEngineService scriptEngine = component.getScriptService();
            bodyContent = scriptEngine.evaluate(contentDynamicExpression, context, message)
                    .orElseThrow(() -> new MailMessageConfigurationException(MAIL_BODY_EMPTY_ERROR.format()));
        }

        String charset = charsetFrom(body);

        return Pair.create(charset, bodyContent);
    }

    protected String charsetFrom(BodyDefinition definition) {
        if (definition == null) return StandardCharsets.UTF_8.toString();
        if (isBlank(definition.getCharset())) return StandardCharsets.UTF_8.toString();
        else return definition.getCharset();
    }
}
