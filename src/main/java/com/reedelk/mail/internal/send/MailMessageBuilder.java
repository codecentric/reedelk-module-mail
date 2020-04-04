package com.reedelk.mail.internal.send;

import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

        // Mandatory
        String from = scriptService.evaluate(this.from, context, message)
                .orElseThrow(() -> new ESBException("From could not be evaluated"));
        mailMessage.setFrom(new InternetAddress(from));

        // Mandatory
        String to = scriptService.evaluate(this.to, context, message)
                .orElseThrow(() -> new ESBException("To could not be evaluated."));
        mailMessage.setRecipients(TO, InternetAddress.parse(to, false));

        // Optional
        scriptService.evaluate(this.cc, context, message).ifPresent(cc -> {
            try {
                mailMessage.setRecipients(CC, InternetAddress.parse(cc, false));
            } catch (MessagingException exception) {
                throw new ESBException("CC could not be evaluated");
            }
        });

        // Optional
        scriptService.evaluate(this.bcc, context, message).ifPresent(bcc -> {
            try {
                mailMessage.setRecipients(BCC, InternetAddress.parse(bcc, false));
            } catch (MessagingException exception) {
                throw new ESBException("BCC could not be evaluated");
            }
        });

        // Optional
        scriptService.evaluate(this.replyTo, context, message).ifPresent(replyTo -> {
            try {
                mailMessage.setReplyTo(InternetAddress.parse(replyTo, false));
            } catch (MessagingException exception) {
                throw new ESBException(exception);
            }
        });

        // Optional
        scriptService.evaluate(this.subject, context, message).ifPresent(subject -> {
            try {
                mailMessage.setSubject(subject, StandardCharsets.UTF_8.toString());
            } catch (MessagingException exceptions) {
                throw new ESBException(exceptions);
            }
        });

        Multipart multipart = new MimeMultipart();
        mailMessage.setContent(multipart);
        mailMessage.setSentDate(new Date());
        return mailMessage;
    }
}
