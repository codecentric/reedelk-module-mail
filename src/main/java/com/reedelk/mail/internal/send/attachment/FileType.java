package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileType extends AbstractAttachmentSourceStrategy {

    @Override
    MimeBodyPart buildInternal(ScriptEngineService scriptEngine, AttachmentDefinition definition, FlowContext context, Message message) throws MessagingException {
        String charset = definition.getCharset();
        String attachmentName = definition.getName();
        String contentType = definition.getContentType();

        final String attachmentContentType = ContentType.from(contentType, charset);
        final String contentTransferEncoding = definition.getContentTransferEncoding();

        MimeBodyPart part = new MimeBodyPart();

        // TODO: Not if present, it should throw exception if not present!
        scriptEngine.evaluate(definition.getFile(), context, message).ifPresent(filePathAndName -> {
            try {
                Path theFilePath = Paths.get(filePathAndName);
                byte[] data = Files.readAllBytes(theFilePath);
                ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);
                dataSource.setName(attachmentName);
                part.setFileName(theFilePath.getFileName().toString());
                part.setDataHandler(new DataHandler(dataSource));
            } catch (MessagingException | IOException exception) {
                throw new ESBException(exception);
            }
        });

        part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
        return part;
    }
}
