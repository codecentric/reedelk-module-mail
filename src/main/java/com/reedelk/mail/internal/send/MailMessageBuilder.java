package com.reedelk.mail.internal.send;

import com.reedelk.mail.internal.exception.NotValidAddressException;
import com.reedelk.mail.internal.exception.NotValidSubjectException;
import com.reedelk.runtime.api.commons.Unchecked;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.mail.Message.RecipientType.*;

public class MailMessageBuilder {

    private final Session session;
    private ScriptEngineService scriptService;

    private DynamicString from;
    private DynamicString to;
    private DynamicString cc;
    private DynamicString bcc;
    private DynamicString replyTo;
    private DynamicString subject;

    private FlowContext context;
    private com.reedelk.runtime.api.message.Message message;

    private MailMessageBuilder(Session session) {
        this.session = session;
    }

    public static MailMessageBuilder get(Session session) {
        return new MailMessageBuilder(session);
    }

    public MailMessageBuilder to(DynamicString to) {
        this.to = to;
        return this;
    }

    public MailMessageBuilder cc(DynamicString cc) {
        this.cc = cc;
        return this;
    }

    public MailMessageBuilder bcc(DynamicString bcc) {
        this.bcc = bcc;
        return this;
    }

    public MailMessageBuilder from(DynamicString from) {
        this.from = from;
        return this;
    }

    public MailMessageBuilder context(FlowContext context) {
        this.context = context;
        return this;
    }

    public MailMessageBuilder replyTo(DynamicString replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public MailMessageBuilder subject(DynamicString subject) {
        this.subject = subject;
        return this;
    }

    public MailMessageBuilder scriptService(ScriptEngineService scriptService) {
        this.scriptService = scriptService;
        return this;
    }

    public MailMessageBuilder message(com.reedelk.runtime.api.message.Message message) {
        this.message = message;
        return this;
    }


    public Message build() throws MessagingException {

        MimeMessage mailMessage = new MimeMessage(session);
        mailMessage.setSentDate(new Date());

        // Mandatory
        String evaluatedFrom = scriptService.evaluate(from, context, message)
                .orElseThrow(() -> new NotValidAddressException(FROM_ERROR.format(from.toString())));
        mailMessage.setFrom(new InternetAddress(evaluatedFrom));

        // Mandatory
        String evaluatedTo = scriptService.evaluate(to, context, message)
                .orElseThrow(() -> new NotValidAddressException(TO_ERROR.format(to.toString())));
        mailMessage.setRecipients(TO, InternetAddress.parse(evaluatedTo));

        // Optional
        scriptService.evaluate(cc, context, message).ifPresent(
                Unchecked.consumer(cc -> mailMessage.setRecipients(CC, InternetAddress.parse(cc)),
                        (cc, exception) -> new NotValidAddressException(exception, CC_ERROR.format(cc, this.cc.toString()))));

        // Optional
        scriptService.evaluate(bcc, context, message).ifPresent(
                Unchecked.consumer(bcc -> mailMessage.setRecipients(BCC, InternetAddress.parse(bcc)),
                        (bcc, exception) -> new NotValidAddressException(exception, BCC_ERROR.format(bcc, this.bcc.toString()))));

        // Optional
        scriptService.evaluate(replyTo, context, message).ifPresent(
                Unchecked.consumer(replyTo -> mailMessage.setReplyTo(InternetAddress.parse(replyTo)),
                        (replyTo, exception) -> new NotValidAddressException(exception, REPLY_TO_ERROR.format(replyTo, this.replyTo.toString()))));

        // Optional
        scriptService.evaluate(subject, context, message).ifPresent(
                Unchecked.consumer(subject -> mailMessage.setSubject(subject, UTF_8.toString()),
                        (subject, exception) -> new NotValidSubjectException(exception, SUBJECT_ERROR.format(subject, this.subject.toString()))));

        return mailMessage;
    }
}
