package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.exception.ESBException;
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
import java.io.Serializable;
import java.util.*;

import static com.reedelk.mail.internal.send.MailSendAttributes.*;

public class MailMessageToMessageMapper {

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message mail) {
        try {
            MimeMessage mimeMessage = (MimeMessage) mail;
            MimeMessageParser parser = new MimeMessageParser(mimeMessage);
            MimeMessageParser parsed = parser.parse();

            MessageBuilder messageBuilder = MessageBuilder.get();

            if (parsed.hasHtmlContent()) {
                StringContent htmlContent = new StringContent(parsed.getHtmlContent(), MimeType.TEXT_HTML);
                messageBuilder.withTypedContent(htmlContent);
            } else if (parsed.hasPlainContent()){
                StringContent plainContent = new StringContent(parsed.getPlainContent(), MimeType.TEXT_PLAIN);
                messageBuilder.withTypedContent(plainContent);
            } else {
                messageBuilder.empty();
            }

            HashMap<String, Attachment> attachmentMap = new HashMap<>();

            List<DataSource> attachmentList = parser.getAttachmentList();
            attachmentList.forEach(dataSource -> processAttachment(attachmentMap, dataSource));

            Map<String, Serializable> attributesMap = new HashMap<>();
            ATTACHMENTS.set(attributesMap, attachmentMap);
            MESSAGE_NUMBER.set(attributesMap, mail.getMessageNumber());
            FROM.set(attributesMap, parsed.getFrom());
            SUBJECT.set(attributesMap, parsed.getSubject());
            REPLY_TO.set(attributesMap, parsed.getReplyTo());
            TO.set(attributesMap, Address.asSerializableList(parsed.getTo()));
            CC.set(attributesMap, Address.asSerializableList(parsed.getCc()));
            BCC.set(attributesMap, Address.asSerializableList(parsed.getBcc()));
            if (mail.getSentDate() != null) SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
            if (mail.getReceivedDate() != null) RECEIVED_DATE.set(attributesMap, mail.getReceivedDate().getTime());

            MessageAttributes messageAttributes = new DefaultMessageAttributes(componentClazz, attributesMap);
            messageBuilder.attributes(messageAttributes);

            return messageBuilder.build();

        } catch (Exception exception) {
            throw new ESBException(exception);
        }
    }

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message[] mails) throws Exception {
        List<Map> messages = new ArrayList<>();

        for (javax.mail.Message m : mails) {
            MimeMessage mimeMessage = (MimeMessage) m;
            MimeMessageParser parser = new MimeMessageParser(mimeMessage);
            MimeMessageParser parsed = parser.parse();

            HashMap<String, Attachment> attachmentMap = new HashMap<>();

            List<DataSource> attachmentList = parser.getAttachmentList();
            attachmentList.forEach(dataSource -> processAttachment(attachmentMap, dataSource));

            Map<String,Serializable> message = new HashMap<>();
            ATTACHMENTS.set(message, attachmentMap);
            MESSAGE_NUMBER.set(message, m.getMessageNumber());
            FROM.set(message, parsed.getFrom());
            SUBJECT.set(message, parsed.getSubject());
            REPLY_TO.set(message, parsed.getReplyTo());
            TO.set(message, Address.asSerializableList(parsed.getTo()));
            CC.set(message, Address.asSerializableList(parsed.getCc()));
            BCC.set(message, Address.asSerializableList(parsed.getBcc()));
            if (m.getSentDate() != null) SENT_DATE.set(message, m.getSentDate().getTime());
            if (m.getReceivedDate() != null) RECEIVED_DATE.set(message, m.getReceivedDate().getTime());

            if (parsed.hasHtmlContent()) {
                StringContent htmlContent = new StringContent(parsed.getHtmlContent(), MimeType.TEXT_HTML);
                message.put("body", htmlContent);
            } else if (parsed.hasPlainContent()){
                StringContent plainContent = new StringContent(parsed.getPlainContent(), MimeType.TEXT_PLAIN);
                message.put("body", plainContent);
            } else {
                message.put("body", null);
            }

            messages.add(message);
        }

        return MessageBuilder.get().withJavaCollection(messages, Map.class).build();
    }

    private static void processAttachment(HashMap<String, Attachment> attachmentMap, DataSource dataSource) {
        try {
            MimeType attachmentMimeType = MimeType.parse(dataSource.getContentType().toLowerCase());
            byte[] attachmentData = ByteArrayUtils.from(dataSource.getInputStream());

            Attachment attachment = Attachment.builder()
                    .content(new ByteArrayContent(attachmentData, attachmentMimeType))
                    .build();
            String attachmentName = attachmentNameFrom(attachmentMimeType, dataSource.getName());
            attachmentMap.put(attachmentName, attachment);
        } catch (IOException e) {
            // TODO: Fixme
            // Fail silently? With a warning? Or throw an exception ?
            e.printStackTrace();
        }
    }

    private static String attachmentNameFrom(MimeType mimeType, String name) {
        // The name could be null when we have an attachment with forwarded.
        return Optional.ofNullable(name).orElseGet(() -> mimeType
                .getFileExtensions()
                .stream()
                .findFirst()
                .map(ext -> UUID.randomUUID().toString() + "." + ext)
                .orElse(UUID.randomUUID().toString() + ".dat"));

    }
}
