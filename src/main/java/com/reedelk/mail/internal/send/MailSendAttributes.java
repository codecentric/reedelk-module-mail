package com.reedelk.mail.internal.send;

import java.io.Serializable;
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
}
