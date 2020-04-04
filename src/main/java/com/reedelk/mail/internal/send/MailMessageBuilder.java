package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.BodyConfiguration;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static java.util.Optional.ofNullable;
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
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });

        // Optional
        String charset = ofNullable(body.getCharset()).orElse(StandardCharsets.UTF_8.toString());
        scriptService.evaluate(this.subject, context, message).ifPresent(subject -> {
            try {
                mailMessage.setSubject(subject, "UTF-8");
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });

        Multipart multipart = new MimeMultipart();

        buildBody(multipart);

        mailMessage.setContent(multipart);
        mailMessage.setSentDate(new Date());
        return mailMessage;
    }

    private void buildBody(Multipart multipart) throws MessagingException {
        DynamicString content = body.getContent();
        scriptService.evaluate(content, context, message).ifPresent(evaluatedBody -> {
            try {
                String contentTypeWithCharset = contentTypeWithCharset();
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(evaluatedBody, contentTypeWithCharset);
                /**
                 * Content-Transfer-Encoding := "BASE64" / "QUOTED-PRINTABLE" /
                 *                              "8BIT"   / "7BIT" /
                 *                              "BINARY" / x-token
                 */
                mimeBodyPart.addHeader("Content-Transfer-Encoding", "asdf");
                multipart.addBodyPart(mimeBodyPart);
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });
    }

    private void attachAttachments(MimeMessage mailMessage) {
        byte[] data = new byte[0];
        MimeBodyPart att = new MimeBodyPart();
        ByteArrayDataSource bds = new ByteArrayDataSource(data, "text/plain");
        bds.setName("attachmentName");
        try {
            att.setDataHandler(new DataHandler(bds));
            att.setFileName(bds.getName());
        } catch (MessagingException e) {
            throw new ESBException(e);
        }
    }

    private String contentTypeWithCharset() {
        String charset = ofNullable(body.getCharset()).orElse(StandardCharsets.UTF_8.toString());
        String contentType = ofNullable(body.getContentType()).orElse(MimeType.TEXT_PLAIN.toString());
        return contentType + "; charset=" + charset;
    }
}
