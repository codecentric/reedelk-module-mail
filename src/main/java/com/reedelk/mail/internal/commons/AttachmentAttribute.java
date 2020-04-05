package com.reedelk.mail.internal.commons;

import java.util.Map;

public enum AttachmentAttribute {

    CHARSET("charset"),
    FILENAME("filename"),
    CONTENT_TYPE("content-type"),
    CONTENT_TRANSFER_ENCODING("content-transfer-encoding");

    private final String attributeName;

    AttachmentAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    public String get(Map<String, String> attributes) {
        return attributes.get(attributeName);
    }

    public void set(Map<String, String> attributes, String value) {
        attributes.put(attributeName, value);
    }
}
