package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.mail.internal.exception.AttachmentConfigurationException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME;

public class ExpressionType implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                              AttachmentDefinition definition,
                              FlowContext context,
                              Message message) {
        String charset = definition.getCharset();
        String contentType = definition.getContentType();
        String attachmentContentType = ContentType.from(contentType, charset);
        String contentTransferEncoding = definition.getContentTransferEncoding();

        MimeBodyPart part = new MimeBodyPart();

        // We accept the fact that we can send an empty file.
        ByteArrayDataSource dataSource = scriptEngine.evaluate(definition.getExpression(), context, message)
                .map(bytes -> new ByteArrayDataSource(bytes, attachmentContentType))
                .orElse(new ByteArrayDataSource(new byte[0], attachmentContentType));

        // The file name is mandatory, otherwise the attachment cannot be sent.
        String fileName = scriptEngine.evaluate(definition.getFileName(), context, message)
                .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_NAME.format(definition.toString())));

        try {
            part.setFileName(fileName);
            part.setDataHandler(new DataHandler(dataSource));
            part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            return part;
        } catch (MessagingException exception) {
            throw new AttachmentConfigurationException(exception.getMessage(), exception);
        }
    }
}
