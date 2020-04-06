package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.internal.commons.AttachmentAttribute;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Map;

public class AttachmentObjectType implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                              ConverterService converterService,
                              String attachmentName,
                              Attachment attachment) {
        Map<String, String> attachmentAttributes = attachment.getAttributes();
        String charset = AttachmentAttribute.CHARSET.get(attachmentAttributes);
        String filename = AttachmentAttribute.FILENAME.get(attachmentAttributes);
        String contentType = AttachmentAttribute.CONTENT_TYPE.get(attachmentAttributes);
        String contentTransferEncoding = AttachmentAttribute.CONTENT_TRANSFER_ENCODING.get(attachmentAttributes);

        String contentTypeWithCharset = ContentType.from(contentType, charset);

        Object data = attachment.content().data();
        byte[] bytes = converterService.convert(data, byte[].class);

        MimeBodyPart part = new MimeBodyPart();
        try {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(bytes, contentTypeWithCharset);
            dataSource.setName(attachmentName);

            part.setFileName(filename);
            part.setDataHandler(new DataHandler(dataSource));
            part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            return part;
        } catch (MessagingException e) {
            throw new ESBException(e);
        }
    }
}
