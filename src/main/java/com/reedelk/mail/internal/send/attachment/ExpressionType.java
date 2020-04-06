package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.mail.internal.exception.AttachmentConfigurationException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNullOrBlank;

public class ExpressionType implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                              AttachmentDefinition definition,
                              FlowContext context,
                              Message message) {
        final String charset = definition.getCharset();
        final String contentType = definition.getContentType();
        final String contentTransferEncoding = definition.getContentTransferEncoding();

        DynamicString fileName = definition.getFileName();
        if (isNullOrBlank(fileName)) {
            throw new AttachmentConfigurationException(ATTACHMENT_FILE_NAME.format(fileName.toString()));
        }

        // The file name is mandatory, otherwise the attachment cannot be sent.
        String finalFileName = scriptEngine.evaluate(fileName, context, message)
                .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_NAME.format(fileName.toString())));

        // We accept the fact that we can send an empty file.
        final String attachmentContentType = ContentType.from(contentType, charset);
        ByteArrayDataSource dataSource = scriptEngine.evaluate(definition.getExpression(), context, message)
                .map(bytes -> new ByteArrayDataSource(bytes, attachmentContentType))
                .orElse(new ByteArrayDataSource(new byte[0], attachmentContentType));

        try {

            MimeBodyPart part = new MimeBodyPart();
            part.setFileName(finalFileName);
            part.setDataHandler(new DataHandler(dataSource));
            part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            return part;

        } catch (MessagingException exception) {
            throw new AttachmentConfigurationException(exception.getMessage(), exception);
        }
    }
}
