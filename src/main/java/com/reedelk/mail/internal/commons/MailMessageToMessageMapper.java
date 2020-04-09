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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message[] mails) {
        return MessageBuilder.get().empty().build();
    }

    private static void processAttachment(HashMap<String, Attachment> attachmentMap, DataSource dataSource) {
        try {
            byte[] attachmentData = ByteArrayUtils.from(dataSource.getInputStream());
            Attachment attachment = Attachment.builder()
                    .content(new ByteArrayContent(attachmentData, MimeType.parse(dataSource.getContentType().toLowerCase())))
                    .build();
            attachmentMap.put(dataSource.getName(), attachment);
        } catch (IOException e) {
            // TODO: Fixme
            // Fail silently? With a warning? Or throw an exception ?
            e.printStackTrace();
        }
    }
}
