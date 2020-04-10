package com.reedelk.mail.internal.smtp.type;

import com.reedelk.mail.component.SMTPMailSend;
import com.reedelk.mail.component.smtp.BodyDefinition;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.MAIL_BODY_EMPTY_ERROR;
import static java.util.Optional.ofNullable;

public class MailSimple extends AbstractMailType {

    public MailSimple(SMTPMailSend component) {
        super(component);
    }

    @Override
    public Email create(FlowContext context, Message message) throws EmailException {

        Email email = new SimpleEmail();

        configureConnection(email);
        configureBaseMessage(context, message, email);

        // Text/Plain Body
        BodyDefinition body = component.getBody();

        // The Body is optional. We can send a Mail without body content.
        DynamicString content = ofNullable(body)
                .flatMap(bodyDefinition -> ofNullable(bodyDefinition.getContent()))
                .orElse(null);

        ScriptEngineService scriptEngine = component.getScriptService();
        String bodyContent = scriptEngine.evaluate(content, context, message)
                .orElseThrow(() -> new MailMessageConfigurationException(MAIL_BODY_EMPTY_ERROR.format()));
        String charset = charsetFrom(body);

        email.setCharset(charset);
        email.setMsg(bodyContent);
        return email;
    }
}
