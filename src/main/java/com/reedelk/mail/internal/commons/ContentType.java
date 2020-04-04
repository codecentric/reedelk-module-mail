package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.message.content.MimeType;

import java.nio.charset.StandardCharsets;

import static java.util.Optional.ofNullable;

public class ContentType {

    public static String from(String contentType, String charset) {
        String theCharset = ofNullable(contentType).orElse(StandardCharsets.UTF_8.toString());
        String theContentType = ofNullable(charset).orElse(MimeType.TEXT_PLAIN.toString());
        return theContentType + "; charset=" + theCharset;
    }
}
