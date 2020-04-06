package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.mail.internal.exception.AttachmentConfigurationException;
import com.reedelk.runtime.api.commons.StreamUtils;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME_EMPTY;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class ResourceType implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                              AttachmentDefinition definition,
                              FlowContext context,
                              Message message) {
        final String charset = definition.getCharset();
        final String contentType = definition.getContentType();
        final DynamicString fileName = definition.getFileName();
        final String contentTransferEncoding = definition.getContentTransferEncoding();
        final ResourceBinary resourceFile = definition.getResourceFile();

        checkState(resourceFile != null, "resource file");

        Path theResourceFilePath = Paths.get(resourceFile.path());

        String finalFileName;
        if (isNotNullOrBlank(fileName)) {
            // We take the final file name from the file name field.
            finalFileName = scriptEngine.evaluate(fileName, context, message)
                    .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_NAME_EMPTY.format(fileName.toString())));
        } else {
            // Otherwise the file name from the resource path.
            finalFileName = theResourceFilePath.getFileName().toString();
        }

        try {
            byte[] data = StreamUtils.FromByteArray.consume(resourceFile.data());
            String attachmentContentType = ContentType.from(contentType, charset);
            ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);

            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(dataSource));
            part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
            part.setFileName(finalFileName);
            return part;
        } catch (MessagingException exception) {
            throw new ESBException(exception);
        }
    }
}
