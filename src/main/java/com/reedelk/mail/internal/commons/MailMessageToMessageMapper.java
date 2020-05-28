package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.exception.MailAttachmentException;
import com.reedelk.mail.internal.type.MailMessage;
import com.reedelk.runtime.api.commons.ByteArrayUtils;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.reedelk.runtime.api.commons.Unchecked.consumer;

public class MailMessageToMessageMapper {

    private MailMessageToMessageMapper() {
    }

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message mail) throws Exception {
        MailMessage mailMessage = createMailMessage(mail);
        return MessageBuilder.get(componentClazz)
                .withJavaObject(mailMessage)
                .build();
    }

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message[] mails) throws Exception {
        List<MailMessage> messages = new ArrayList<>();
        for (javax.mail.Message mail : mails) {
            MailMessage message = createMailMessage(mail);
            messages.add(message);
        }
        return MessageBuilder.get(componentClazz)
                .withList(messages, MailMessage.class)
                .build();
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

    private static HashMap<String, Attachment> createAttachmentsMap(MimeMessageParser parser) {
        HashMap<String, Attachment> attachmentMap = new HashMap<>();
        List<DataSource> attachmentList = parser.getAttachmentList();
        attachmentList.forEach(consumer((dataSource) -> processAttachment(attachmentMap, dataSource),
                (dataSource, exception) -> new MailAttachmentException(exception.getMessage(), exception)));
        return attachmentMap;
    }

    private static MailMessage createMailMessage(javax.mail.Message mail) throws Exception {
        MimeMessage mimeMessage = (MimeMessage) mail;
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        MimeMessageParser parsed = parser.parse();
        HashMap<String, Attachment> attachmentMap = createAttachmentsMap(parser);
        return new MailMessage(mail, parsed, attachmentMap);
    }
}
