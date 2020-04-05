package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.BodyDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.mail.internal.exception.NotValidBodyException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.nio.charset.StandardCharsets;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.MAIL_BODY_ERROR;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static java.util.Optional.ofNullable;

public class MailBodyBuilder {

    private final BodyDefinition definition;

    private Message message;
    private FlowContext context;
    private ScriptEngineService scriptEngine;

    private MailBodyBuilder(BodyDefinition definition) {
        this.definition = definition;
    }

    public static MailBodyBuilder get(BodyDefinition definition) {
        return new MailBodyBuilder(definition);
    }

    public MailBodyBuilder withMessage(Message message) {
        this.message = message;
        return this;
    }

    public MailBodyBuilder withFlowContext(FlowContext context) {
        this.context = context;
        return this;
    }

    public MailBodyBuilder withScriptEngine(ScriptEngineService scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    public void build(Multipart multipart) {
        // The Body is optional. We can send a Mail without body content.
        DynamicString content = ofNullable(definition)
                .flatMap(bodyDefinition -> ofNullable(bodyDefinition.getContent()))
                .orElse(null);

        String bodyContent = scriptEngine.evaluate(content, context, message).orElse(EMPTY);
        String charset = charsetFrom(definition);
        String contentType = contentTypeFrom(definition);
        String contentTransferEncoding = contentTransferEncodingFrom(definition);
        String contentTypeWithCharset = ContentType.from(contentType, charset);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeBodyPart.setContent(bodyContent, contentTypeWithCharset);
            mimeBodyPart.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            multipart.addBodyPart(mimeBodyPart);
        } catch (MessagingException exception) {
            String error = MAIL_BODY_ERROR.format(bodyContent, content == null ? null : content.toString());
            throw new NotValidBodyException(error, exception);
        }
    }

    private String charsetFrom(BodyDefinition definition) {
        if (definition == null) return StandardCharsets.UTF_8.toString();
        if (isBlank(definition.getCharset())) return StandardCharsets.UTF_8.toString();
        else return definition.getCharset();
    }

    private String contentTypeFrom(BodyDefinition definition) {
        if (definition == null) return MimeType.AsString.TEXT_PLAIN;
        if (isBlank(definition.getContentType())) return MimeType.AsString.TEXT_PLAIN;
        else return definition.getContentType();
    }

    private String contentTransferEncodingFrom(BodyDefinition definition) {
        if (definition == null) return "7bit";
        if (isBlank(definition.getContentTransferEncoding())) return "7bit";
        else return definition.getContentTransferEncoding();
    }
}
