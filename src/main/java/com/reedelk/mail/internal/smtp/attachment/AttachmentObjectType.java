package com.reedelk.mail.internal.smtp.attachment;

import com.reedelk.mail.internal.commons.AttachmentAttribute;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.exception.MailAttachmentException;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.script.ScriptEngineService;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.util.ByteArrayDataSource;
import java.util.Map;

public class AttachmentObjectType implements AttachmentSourceStrategy {

    @Override
    public void build(ScriptEngineService scriptEngine,
                      ConverterService converterService,
                      MultiPartEmail email,
                      String attachmentName,
                      Attachment attachment) {

        Map<String, String> attachmentAttributes = attachment.attributes();
        String charset = AttachmentAttribute.CHARSET.get(attachmentAttributes);
        String filename = AttachmentAttribute.FILENAME.get(attachmentAttributes);
        String contentType = AttachmentAttribute.CONTENT_TYPE.get(attachmentAttributes);

        String contentTypeWithCharset = ContentType.from(contentType, charset);

        Object data = attachment.data();

        byte[] bytes = converterService.convert(data, byte[].class);

        try {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(bytes, contentTypeWithCharset);

            dataSource.setName(attachmentName);

            email.attach(dataSource, filename, attachmentName);

        } catch (EmailException exception) {
            throw new MailAttachmentException(exception.getMessage(), exception);
        }
    }
}
