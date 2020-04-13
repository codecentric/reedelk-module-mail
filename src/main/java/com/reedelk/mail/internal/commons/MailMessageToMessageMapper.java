package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.exception.MailAttachmentException;
import com.reedelk.mail.internal.smtp.MailSendAttributes;
import com.reedelk.runtime.api.commons.ByteArrayUtils;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.message.DefaultMessageAttributes;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.StringContent;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import static com.reedelk.runtime.api.commons.Unchecked.consumer;

public class MailMessageToMessageMapper {

    private static final String MAIL_MESSAGE_MAP_BODY = "body";

    private MailMessageToMessageMapper() {
    }

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message mail) throws Exception {
        MimeMessage mimeMessage = (MimeMessage) mail;
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        MimeMessageParser parsed = parser.parse();

        MessageBuilder messageBuilder = MessageBuilder.get();
        if (parsed.hasHtmlContent()) {
            StringContent htmlContent = new StringContent(parsed.getHtmlContent(), MimeType.TEXT_HTML);
            messageBuilder.withTypedContent(htmlContent);
        } else if (parsed.hasPlainContent()) {
            StringContent plainContent = new StringContent(parsed.getPlainContent(), MimeType.TEXT_PLAIN);
            messageBuilder.withTypedContent(plainContent);
        } else {
            messageBuilder.empty();
        }

        HashMap<String, Attachment> attachmentMap = createAttachmentsMap(parser);
        Map<String, Serializable> attributesMap = MailSendAttributes.from(mail, parsed, attachmentMap);

        MessageAttributes messageAttributes = new DefaultMessageAttributes(componentClazz, attributesMap);
        messageBuilder.attributes(messageAttributes);
        return messageBuilder.build();
    }

    @SuppressWarnings({"rawtypes"})
    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message[] mails) throws Exception {
        List<Map> messages = new ArrayList<>();
        for (javax.mail.Message mail : mails) {
            Map<String, Serializable> message = createMailMessageAsMap(mail);
            messages.add(message);
        }
        MessageAttributes attributes = new DefaultMessageAttributes(componentClazz, ImmutableMap.of());
        return MessageBuilder.get()
                .attributes(attributes)
                .withList(messages, Map.class).build();
    }

    private static void processAttachment(HashMap<String, Attachment> attachmentMap, DataSource dataSource) throws IOException {
        MimeType attachmentMimeType = MimeType.parse(dataSource.getContentType().toLowerCase());
        try (InputStream inputStream = dataSource.getInputStream()) {
            byte[] attachmentData = ByteArrayUtils.from(inputStream);
            String attachmentName = attachmentNameFrom(attachmentMimeType, dataSource.getName());
            Attachment attachment = Attachment.builder()
                    .name(attachmentName)
                    .content(new ByteArrayContent(attachmentData, attachmentMimeType))
                    .build();
            attachmentMap.put(attachmentName, attachment);
        }
    }

    private static String attachmentNameFrom(MimeType mimeType, String name) {
        // The name could be null for example when we have a forwarded message with an attachment.
        // If the name is null we come up with a name and an extension for the attachment: in this
        // case the name is a random UUID and if the mime type is known and there are extensions
        // associated with it we use that extension, otherwise .dat extension.
        return Optional.ofNullable(name)
                .orElseGet(() -> mimeType
                        .getFileExtensions()
                        .stream()
                        .findFirst()
                        .map(ext -> UUID.randomUUID().toString() + "." + ext)
                        .orElse(UUID.randomUUID().toString() + "." + Defaults.UNKNOWN_ATTACHMENT_MIME_EXTENSION));
    }

    private static Map<String, Serializable> createMailMessageAsMap(javax.mail.Message mail) throws Exception {
        MimeMessage mimeMessage = (MimeMessage) mail;
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        MimeMessageParser parsed = parser.parse();

        HashMap<String, Attachment> attachmentMap = createAttachmentsMap(parser);
        Map<String, Serializable> message = MailSendAttributes.from(mail, parsed, attachmentMap);
        if (parsed.hasHtmlContent()) {
            StringContent htmlContent = new StringContent(parsed.getHtmlContent(), MimeType.TEXT_HTML);
            message.put(MAIL_MESSAGE_MAP_BODY, htmlContent);
        } else if (parsed.hasPlainContent()) {
            StringContent plainContent = new StringContent(parsed.getPlainContent(), MimeType.TEXT_PLAIN);
            message.put(MAIL_MESSAGE_MAP_BODY, plainContent);
        } else {
            message.put(MAIL_MESSAGE_MAP_BODY, null);
        }
        return message;
    }

    private static HashMap<String, Attachment> createAttachmentsMap(MimeMessageParser parser) {
        HashMap<String, Attachment> attachmentMap = new HashMap<>();
        List<DataSource> attachmentList = parser.getAttachmentList();
        attachmentList.forEach(consumer((dataSource) -> processAttachment(attachmentMap, dataSource),
                (dataSource, exception) -> new MailAttachmentException(exception.getMessage(), exception)));
        return attachmentMap;
    }
}
