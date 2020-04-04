package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.BodyDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

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
        DynamicString content = definition.getContent();
        scriptEngine.evaluate(content, context, message).ifPresent(evaluatedBody -> {

            try {
                final String charset = definition.getCharset();
                final String contentType = definition.getContentType();
                final String contentTransferEncoding = definition.getContentTransferEncoding();
                final String contentTypeWithCharset = ContentType.from(contentType, charset);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(evaluatedBody, contentTypeWithCharset);
                mimeBodyPart.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
                multipart.addBodyPart(mimeBodyPart);
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });
    }
}
