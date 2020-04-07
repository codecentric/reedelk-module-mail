package com.reedelk.mail.internal.send.type;

import com.reedelk.mail.component.BodyDefinition;
import com.reedelk.runtime.api.message.content.MimeType;

import java.nio.charset.StandardCharsets;

import static com.reedelk.runtime.api.commons.StringUtils.isBlank;

abstract class AbstractMailType implements MailTypeStrategy {

    protected String charsetFrom(BodyDefinition definition) {
        if (definition == null) return StandardCharsets.UTF_8.toString();
        if (isBlank(definition.getCharset())) return StandardCharsets.UTF_8.toString();
        else return definition.getCharset();
    }

    protected String contentTypeFrom(BodyDefinition definition) {
        if (definition == null) return MimeType.AsString.TEXT_PLAIN;
        if (isBlank(definition.getContentType())) return MimeType.AsString.TEXT_PLAIN;
        else return definition.getContentType();
    }
}
