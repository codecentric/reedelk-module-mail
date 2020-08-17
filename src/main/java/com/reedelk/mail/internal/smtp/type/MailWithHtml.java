package com.reedelk.mail.internal.smtp.type;

import com.reedelk.mail.component.SMTPMailSend;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.Pair;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;

public class MailWithHtml extends AbstractMailType {

    public MailWithHtml(SMTPMailSend component, ConverterService converterService) {
        super(component, converterService);
    }

    @Override
    public MailTypeStrategyResult create(FlowContext context, Message message) throws EmailException {

        ImageHtmlEmail email = new ImageHtmlEmail();

        configureConnection(email);
        configureBaseMessage(context, message, email);
        configureAttachments(context, message, email);

        Pair<String, String> charsetAndBody = buildCharsetAndMailBody(context, message);
        email.setCharset(charsetAndBody.left());
        email.setHtmlMsg(charsetAndBody.right());

        return MailTypeStrategyResult.create(email, charsetAndBody.right(), MimeType.TEXT_HTML);
    }
}
