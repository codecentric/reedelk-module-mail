package com.reedelk.mail.internal.send.type;

import com.reedelk.mail.component.BodyDefinition;
import com.reedelk.mail.component.MailSend;
import com.reedelk.mail.internal.send.MailAttachmentBuilder;
import com.reedelk.mail.internal.send.MailMessageBuilder;
import com.reedelk.mail.internal.send.SMTPEmailFactory;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class MailWithAttachments extends AbstractMailType {

    private final MailSend component;

    public MailWithAttachments(MailSend component) {
        this.component = component;
    }

    @Override
    public Email create(FlowContext context, Message message) throws EmailException {

        MultiPartEmail email = new MultiPartEmail();

        SMTPEmailFactory.builder(email)
                .configuration(component.getConnectionConfiguration())
                .build();

        // Base Message
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

        // Body
        BodyDefinition body = component.getBody();

        // The Body is optional. We can send a Mail without body content.
        DynamicString content = ofNullable(body)
                .flatMap(bodyDefinition -> ofNullable(bodyDefinition.getContent()))
                .orElse(null);

        ScriptEngineService scriptEngine = component.getScriptService();
        String bodyContent = scriptEngine.evaluate(content, context, message).orElse(EMPTY);
        String charset = charsetFrom(body);

        email.setCharset(charset);
        email.setMsg(bodyContent);


        // Attachments
        MailAttachmentBuilder.get(email)
                .attachmentsMap(component.getAttachmentsMap())
                .converter(component.getConverterService())
                .scriptEngine(component.getScriptService())
                .attachments(component.getAttachments())
                .message(message)
                .context(context)
                .build();

        return email;
    }
}
