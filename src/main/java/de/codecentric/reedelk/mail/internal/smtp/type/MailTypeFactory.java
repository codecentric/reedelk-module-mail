package de.codecentric.reedelk.mail.internal.smtp.type;

import de.codecentric.reedelk.mail.component.SMTPMailSend;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;

import java.util.Optional;

import static de.codecentric.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;

public class MailTypeFactory {

    private MailTypeFactory() {
    }

    public static MailTypeStrategy from(SMTPMailSend component, ConverterService converterService) {

        MimeType mimeType = Optional.ofNullable(component.getBody())
                .flatMap(definition -> Optional.ofNullable(definition.getContentType()))
                .map(String::toLowerCase)
                .map(MimeType::parse)
                .orElse(MimeType.TEXT_PLAIN);

        if (MimeType.TEXT_HTML.equals(mimeType)) {
            // HTML Body and optionally with Attachments (text/html) type.
            return new MailWithHtml(component, converterService);

        } else if (!component.getAttachments().isEmpty() ||
                isNotNullOrBlank(component.getAttachmentsMap())) {
            // Mail With Attachments type.
            return new MailWithAttachments(component, converterService);
        } else {
            // Simple Email (Text Plain) type.
            return new MailSimple(component, converterService);
        }
    }
}
