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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_EMPTY;
import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME_EMPTY;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;

public class FileType implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                              AttachmentDefinition definition,
                              FlowContext context,
                              Message message) {
        final String charset = definition.getCharset();
        final String contentType = definition.getContentType();
        final DynamicString attachmentFile = definition.getFile();
        final DynamicString fileName = definition.getFileName();
        final String contentTransferEncoding = definition.getContentTransferEncoding();

        String filePathAndName = scriptEngine.evaluate(attachmentFile, context, message)
                .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_EMPTY.format(attachmentFile.toString())));

        Path theFilePath = Paths.get(filePathAndName);

        String finalFileName;
        if (isNotNullOrBlank(fileName)) {
            // We take the final file name from the file name field if the user defined it.
            finalFileName = scriptEngine.evaluate(fileName, context, message)
                    .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_NAME_EMPTY.format(fileName.toString())));
        } else {
            // Otherwise the file name from the file path.
            finalFileName = theFilePath.getFileName().toString();
        }

        try {

            byte[] data = Files.readAllBytes(theFilePath);
            String attachmentContentType = ContentType.from(contentType, charset);
            ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);

            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(dataSource));
            part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            part.setFileName(finalFileName);
            return part;

        } catch (IOException | MessagingException exception) {
            throw new AttachmentConfigurationException(exception.getMessage(), exception);
        }
    }
}
