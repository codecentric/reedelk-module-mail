package de.codecentric.reedelk.mail.internal.commons;

import de.codecentric.reedelk.runtime.api.message.content.Attachment;

import java.util.Map;

public enum AttachmentAttribute {

    CHARSET("charset"),
    FILENAME("filename"),
    CONTENT_TYPE("content-type");

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

    public void set(Attachment.Builder builder, String value) {
        builder.attribute(attributeName, value);
    }
}
