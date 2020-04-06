package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;

import javax.mail.Address;
import javax.mail.MessagingException;
import java.util.Date;

public class MailMessageToMessageMapper {

    public static Message map(javax.mail.Message mail) {
        try {
            Address[] from = mail.getFrom();
            Address[] replyTo = mail.getReplyTo();
            Address[] recipients = mail.getRecipients(javax.mail.Message.RecipientType.BCC);
            Date sentDate = mail.getSentDate();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return MessageBuilder.get().empty().build();
    }
}
