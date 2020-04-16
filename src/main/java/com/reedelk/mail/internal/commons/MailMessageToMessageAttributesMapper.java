package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.smtp.MailSendAttributes;
import org.apache.commons.mail.Email;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MailMessageToMessageAttributesMapper {

    private MailMessageToMessageAttributesMapper() {
    }

    public static Map<String, Serializable> from(Email mail) {
        Map<String, Serializable> attributesMap = new HashMap<>();
        MailSendAttributes.SUBJECT.set(attributesMap, mail.getSubject());
        MailSendAttributes.SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
        MailSendAttributes.MESSAGE_NUMBER.set(attributesMap, mail.getMimeMessage().getMessageNumber());
        MailSendAttributes.FROM.set(attributesMap, mail.getFromAddress().toString());
        MailSendAttributes.REPLY_TO.set(attributesMap, Address.asSerializableList(mail.getReplyToAddresses()));
        MailSendAttributes.TO.set(attributesMap, Address.asSerializableList(mail.getToAddresses()));
        MailSendAttributes.CC.set(attributesMap, Address.asSerializableList(mail.getCcAddresses()));
        MailSendAttributes.BCC.set(attributesMap, Address.asSerializableList(mail.getBccAddresses()));
        return attributesMap;
    }
}
