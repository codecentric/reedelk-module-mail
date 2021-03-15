package de.codecentric.reedelk.mail.internal.smtp;

import de.codecentric.reedelk.mail.internal.exception.MailMessageConfigurationException;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import de.codecentric.reedelk.runtime.api.commons.Unchecked;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static de.codecentric.reedelk.mail.internal.commons.Messages.MailSendComponent.*;
import static java.util.Arrays.asList;

public class MailMessageBuilder {

    private final Email email;
    private ScriptEngineService scriptService;

    private DynamicString from;
    private DynamicString to;
    private DynamicString cc;
    private DynamicString bcc;
    private DynamicString replyTo;
    private DynamicString subject;

    private FlowContext context;
    private de.codecentric.reedelk.runtime.api.message.Message message;

    private MailMessageBuilder(Email email) {
        this.email = email;
    }

    public static MailMessageBuilder get(Email email) {
        return new MailMessageBuilder(email);
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

    public MailMessageBuilder message(de.codecentric.reedelk.runtime.api.message.Message message) {
        this.message = message;
        return this;
    }

    public void build() throws EmailException {
        try {
            // Mandatory
            String evaluatedFrom = scriptService.evaluate(from, context, message)
                    .orElseThrow(() -> new MailMessageConfigurationException(FROM_ERROR.format(from.toString())));

            email.setFrom(evaluatedFrom);

            // Mandatory
            String evaluatedTo = scriptService.evaluate(to, context, message)
                    .orElseThrow(() -> new MailMessageConfigurationException(TO_ERROR.format(to.toString())));
            email.setTo(asList(InternetAddress.parse(evaluatedTo)));

            // Optional
            scriptService.evaluate(cc, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(notBlankCC -> email.setCc(asList(InternetAddress.parse(notBlankCC))),
                            (notBlankCC, exception) -> new MailMessageConfigurationException(CC_ERROR.format(notBlankCC, cc.toString()), exception)));

            // Optional
            scriptService.evaluate(bcc, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(notBlankBCC -> email.setBcc(asList(InternetAddress.parse(notBlankBCC))),
                            (notBlankBCC, exception) -> new MailMessageConfigurationException(BCC_ERROR.format(notBlankBCC, bcc.toString()), exception)));

            // Optional
            scriptService.evaluate(replyTo, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(notBlankReplyTo -> email.setReplyTo(asList(InternetAddress.parse(notBlankReplyTo))),
                            (notBlankReplyTo, exception) -> new MailMessageConfigurationException(REPLY_TO_ERROR.format(notBlankReplyTo, replyTo.toString()), exception)));

            // Optional
            scriptService.evaluate(subject, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(email::setSubject,
                            (notBlankSubject, exception) -> new MailMessageConfigurationException(SUBJECT_ERROR.format(notBlankSubject, subject.toString()), exception)));

        } catch (AddressException exception) {
            throw new MailMessageConfigurationException(exception.getMessage(), exception);
        }
    }
}
