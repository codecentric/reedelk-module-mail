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
    SENT_DATE("sentDate");

    private final String attributeName;

    MailSendAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public void set(Map<String, Serializable> attributesMap, Serializable value) {
        attributesMap.put(attributeName, value);
    }
}
