package com.reedelk.mail.internal.smtp.type;

import com.reedelk.mail.component.SMTPMailSend;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Pair;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailSimple extends AbstractMailType {

    public MailSimple(SMTPMailSend component, ConverterService converterService) {
        super(component, converterService);
    }

    @Override
    public Email create(FlowContext context, Message message) throws EmailException {
        Email email = new SimpleEmail();
        configureConnection(email);
        configureBaseMessage(context, message, email);

        Pair<String, String> charsetAndBody = buildCharsetAndMailBody(context, message);
        email.setCharset(charsetAndBody.left());
        email.setMsg(charsetAndBody.right());
        return email;
    }
}
