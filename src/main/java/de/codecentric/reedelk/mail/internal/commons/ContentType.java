package de.codecentric.reedelk.mail.internal.commons;

import de.codecentric.reedelk.runtime.api.message.content.MimeType;

import java.nio.charset.StandardCharsets;

import static java.util.Optional.ofNullable;

public class ContentType {

    private ContentType() {
    }

    public static String from(String contentType, String charset) {
        String theContentType = ofNullable(contentType).orElse(MimeType.TEXT_PLAIN.toString());
        String theCharset = ofNullable(charset).orElse(StandardCharsets.UTF_8.toString());
        return theContentType + "; charset=" + theCharset;
    }
}
