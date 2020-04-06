package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.*;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class MailMessageToMessageMapper {

    public static Message map(Class<? extends Component> componentClazz, javax.mail.Message mail) {
        try {
            MailContent mailContent = extractMail(mail);

            MessageAttributes messageAttributes =
                    MailMessageToMessageAttributesMapper.from(componentClazz, mail, mailContent.attachments);

            return MessageBuilder.get()
                    .attributes(messageAttributes)
                    .withTypedContent(mailContent.body)
                    .build();

        } catch (Exception exception) {
            throw new ESBException(exception);
        }
    }

    static class MailContent {
        ArrayList<Attachment> attachments = new ArrayList<>();
        TypedContent<?,?> body;
    }

    public static MailContent extractMail(javax.mail.Message message) throws Exception {
        MailContent mailContent = new MailContent();

        Object content = message.getContent();
        if (content instanceof String) {
            // No attachments
            MimeType realMimeType = MimeType.parse(message.getContentType().toLowerCase());
            mailContent.body = new StringContent((String) content, realMimeType);
        }

        if (content instanceof Multipart) {
            // There are attachments: First Body Part is the Actual Body
            Multipart multipart = (Multipart) content;


            BodyPart bodyPart = multipart.getBodyPart(0);
            Object bodyContent = bodyPart.getContent();
            if (bodyContent instanceof String) {
                MimeType realMimeType = MimeType.parse(bodyPart.getContentType().toLowerCase());
                mailContent.body = new StringContent((String) bodyContent, realMimeType);
            }

            ArrayList<Attachment> result = new ArrayList<>();
            for (int i = 0; i < multipart.getCount(); i++) {
                result.addAll(extractMail(multipart.getBodyPart(i)));
            }
            mailContent.attachments = result;

        }
        return mailContent;
    }

    private static List<Attachment> extractMail(BodyPart part) throws Exception {
        List<Attachment> result = new ArrayList<>();
        Object content = part.getContent();
        if (content instanceof InputStream || content instanceof String) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) ||
                    isNotBlank(part.getFileName())) {
                byte[] bytes = toByteArray(part.getInputStream());
                String contentType = part.getContentType();
                MimeType realMimeType = MimeType.parse(contentType.toLowerCase());
                ByteArrayContent byteArrayContent = new ByteArrayContent(bytes, realMimeType);
                Attachment attachment = Attachment.builder()
                        .content(byteArrayContent)
                        .build();
                result.add(attachment);
                return result;
            } else {
                String contentType = part.getContentType();
                System.out.println("It is not an attachment");
                return new ArrayList<>();
            }
        }

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.addAll(extractMail(bodyPart));
            }
        }
        return result;
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
