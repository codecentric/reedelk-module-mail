package com.reedelk.mail.internal.send.type;

import com.reedelk.mail.component.BodyDefinition;
import com.reedelk.mail.component.MailSend;
import com.reedelk.runtime.api.message.content.MimeType;

import java.util.Optional;

import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;

public class MailTypeFactory {

    public static MailTypeStrategy from(MailSend component) {
        BodyDefinition body = component.getBody();
        MimeType mimeType = Optional.ofNullable(body)
                .flatMap(definition -> Optional.ofNullable(definition.getContentType()))
                .map(String::toLowerCase)
                .map(MimeType::parse)
                .orElse(MimeType.TEXT_PLAIN);

        // Or default content type of does not exists!
        if (MimeType.TEXT_HTML.equals(mimeType)) {
            // HTML Body and optionally with Attachments (text/html)
            return new MailWithHtml(component);

        } else if (!component.getAttachments().isEmpty() ||
                isNotNullOrBlank(component.getAttachmentsMap())) {
            // Mail With Attachments
            return new MailWithAttachments(component);
        } else {
            // Simple Email (Text Plain)
            return new MailSimple(component);
        }
    }
}
