package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.BodyConfiguration;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static java.util.Optional.*;
import static javax.mail.Message.RecipientType.*;

public class MailMessageBuilder {

    private Session session;

    private ScriptEngineService scriptService;

    private DynamicString from;
    private DynamicString to;
    private DynamicString cc;
    private DynamicString bcc;
    private DynamicString replyTo;
    private DynamicString subject;

    private BodyConfiguration body;

    private FlowContext context;
    private com.reedelk.runtime.api.message.Message message;

    private MailMessageBuilder() {
    }

    public static MailMessageBuilder builder() {
        return new MailMessageBuilder();
    }

    public MailMessageBuilder scriptService(ScriptEngineService scriptService) {
        this.scriptService = scriptService;
        return this;
    }

    public MailMessageBuilder session(Session session) {
        this.session = session;
        return this;
    }

    public MailMessageBuilder from(DynamicString from) {
        this.from = from;
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

    public MailMessageBuilder body(BodyConfiguration body) {
        this.body = body;
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

    public MailMessageBuilder to(DynamicString to) {
        this.to = to;
        return this;
    }

    public MailMessageBuilder message(com.reedelk.runtime.api.message.Message message) {
        this.message = message;
        return this;
    }

    public MailMessageBuilder context(FlowContext context) {
        this.context = context;
        return this;
    }

    public Message build() throws MessagingException, UnsupportedEncodingException {

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
        String cc = scriptService.evaluate(this.cc, context, message)
                .orElseThrow(() -> new ESBException("CC could not be evaluated."));
        mailMessage.setRecipients(CC, InternetAddress.parse(cc, false));

        // Optional
        String bcc = scriptService.evaluate(this.bcc, context, message)
                .orElseThrow(() -> new ESBException("BCC could not be evaluated."));
        mailMessage.setRecipients(BCC, InternetAddress.parse(bcc, false));

        // Optional
        scriptService.evaluate(this.replyTo, context, message).ifPresent(replyTo -> {
            try {
                mailMessage.setReplyTo(InternetAddress.parse(replyTo, false));
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });

        // Optional
        scriptService.evaluate(this.subject, context, message).ifPresent(subject -> {
            try {
                mailMessage.setSubject(subject, "UTF-8");
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });

        buildBody(mailMessage);

        mailMessage.setSentDate(new Date());
        return mailMessage;
    }

    private void buildBody(MimeMessage mailMessage) {
        // TODO: whats the point?
        String contentTransferEncoding = body.getContentTransferEncoding();

        DynamicString content = body.getContent();
        String charset = ofNullable(body.getCharset()).orElse(StandardCharsets.UTF_8.toString());
        scriptService.evaluate(content, context, message).ifPresent(evaluatedBody -> {
            try {
                mailMessage.setText(evaluatedBody, charset);
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });

        String contentType = ofNullable(body.getContentType()).orElse(MimeType.TEXT_PLAIN.toString());
        try {
            mailMessage.addHeader("Content-type", contentType);
        } catch (MessagingException e) {
            throw new ESBException(e);
        }
    }
}
