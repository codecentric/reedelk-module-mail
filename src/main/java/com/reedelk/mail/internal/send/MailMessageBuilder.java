package com.reedelk.mail.internal.send;

import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.commons.Unchecked;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.*;
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
    private com.reedelk.runtime.api.message.Message message;

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

    public MailMessageBuilder message(com.reedelk.runtime.api.message.Message message) {
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
                    .ifPresent(Unchecked.consumer(cc -> email.setCc(asList(InternetAddress.parse(cc))),
                            (cc, exception) -> new MailMessageConfigurationException(CC_ERROR.format(cc, this.cc.toString()), exception)));

            // Optional
            scriptService.evaluate(bcc, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(bcc -> email.setBcc(asList(InternetAddress.parse(bcc))),
                            (bcc, exception) -> new MailMessageConfigurationException(BCC_ERROR.format(bcc, this.bcc.toString()), exception)));

            // Optional
            scriptService.evaluate(replyTo, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(replyTo -> email.setReplyTo(asList(InternetAddress.parse(replyTo))),
                            (replyTo, exception) -> new MailMessageConfigurationException(REPLY_TO_ERROR.format(replyTo, this.replyTo.toString()), exception)));

            // Optional
            scriptService.evaluate(subject, context, message)
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(Unchecked.consumer(email::setSubject,
                            (subject, exception) -> new MailMessageConfigurationException(SUBJECT_ERROR.format(subject, this.subject.toString()), exception)));

        } catch (AddressException exception) {
            throw new MailMessageConfigurationException(exception.getMessage(), exception);
        }
    }
}
