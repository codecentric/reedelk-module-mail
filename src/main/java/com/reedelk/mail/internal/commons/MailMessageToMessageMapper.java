package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.send.MailSendAttributes;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            MailSendAttributes.ATTACHMENTS.set(attributesMap, attachmentMap);
            MailSendAttributes.SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
            MailSendAttributes.MESSAGE_NUMBER.set(attributesMap, mail.getMessageNumber());
            MailSendAttributes.FROM.set(attributesMap, parsed.getFrom());
            MailSendAttributes.SUBJECT.set(attributesMap, parsed.getSubject());
            MailSendAttributes.REPLY_TO.set(attributesMap, parsed.getReplyTo());
            MailSendAttributes.TO.set(attributesMap, Address.asSerializableList(parsed.getTo()));
            MailSendAttributes.CC.set(attributesMap, Address.asSerializableList(parsed.getCc()));
            MailSendAttributes.BCC.set(attributesMap, Address.asSerializableList(parsed.getBcc()));
            MessageAttributes messageAttributes = new DefaultMessageAttributes(componentClazz, attributesMap);
            messageBuilder.attributes(messageAttributes);

            return messageBuilder.build();

        } catch (Exception exception) {
            throw new ESBException(exception);
        }
    }

    private static void processAttachment(HashMap<String, Attachment> attachmentMap, DataSource dataSource) {
        try {
            byte[] attachmentData = toByteArray(dataSource.getInputStream());
            Attachment attachment = Attachment.builder()
                    .content(new ByteArrayContent(attachmentData, MimeType.parse(dataSource.getContentType().toLowerCase())))
                    .build();
            attachmentMap.put(dataSource.getName(), attachment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
