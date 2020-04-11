package com.reedelk.mail.internal.smtp;

import com.reedelk.mail.internal.commons.Address;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Message;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum MailSendAttributes {

    FROM("from"),
    TO("to"),
    CC("cc"),
    BCC("bcc"),
    SUBJECT("subject"),
    REPLY_TO("replyTo"),
    SENT_DATE("sentDate"),
    RECEIVED_DATE("receivedDate"),
    ATTACHMENTS("attachments"),
    MESSAGE_NUMBER("messageNumber");

    private final String attributeName;

    MailSendAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public void set(Map<String, Serializable> attributesMap, Serializable value) {
        attributesMap.put(attributeName, value);
    }

    public static Map<String, Serializable> from(Message mail, MimeMessageParser parsed, Serializable attachments) throws Exception {
        Map<String, Serializable> attributesMap = new HashMap<>();
        FROM.set(attributesMap, parsed.getFrom());
        SUBJECT.set(attributesMap, parsed.getSubject());
        REPLY_TO.set(attributesMap, parsed.getReplyTo());
        TO.set(attributesMap, Address.asSerializableList(parsed.getTo()));
        CC.set(attributesMap, Address.asSerializableList(parsed.getCc()));
        BCC.set(attributesMap, Address.asSerializableList(parsed.getBcc()));
        ATTACHMENTS.set(attributesMap, attachments);
        MESSAGE_NUMBER.set(attributesMap, mail.getMessageNumber());
        if (mail.getSentDate() != null) SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
        if (mail.getReceivedDate() != null) RECEIVED_DATE.set(attributesMap, mail.getReceivedDate().getTime());
        return attributesMap;
    }
}
