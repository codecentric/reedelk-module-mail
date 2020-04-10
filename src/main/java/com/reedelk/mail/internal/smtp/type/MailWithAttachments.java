package com.reedelk.mail.internal.smtp.type;

import com.reedelk.mail.component.SMTPMailSend;
import com.reedelk.mail.component.smtp.BodyDefinition;
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

    public MailWithAttachments(SMTPMailSend component) {
        super(component);
    }

    @Override
    public Email create(FlowContext context, Message message) throws EmailException {

        MultiPartEmail email = new MultiPartEmail();

        configureConnection(email);
        configureBaseMessage(context, message, email);
        configureAttachments(context, message, email);

        // Text/Plain body
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
        return email;
    }
}
