package de.codecentric.reedelk.mail.internal.smtp.attachment;

import de.codecentric.reedelk.mail.component.smtp.AttachmentDefinition;
import de.codecentric.reedelk.mail.internal.commons.ContentType;
import de.codecentric.reedelk.mail.internal.exception.MailAttachmentException;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.codecentric.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_EMPTY;

public class FileType extends AbstractAttachment {

    @Override
    public void build(ScriptEngineService scriptEngine,
                      AttachmentDefinition definition,
                      MultiPartEmail email,
                      FlowContext context,
                      Message message) {

        String name = definition.getName();
        String charset = definition.getCharset();
        String contentType = definition.getContentType();
        DynamicString attachmentFile = definition.getFile();
        DynamicString userDefinedFileName = definition.getFileName();

        String filePathAndName = scriptEngine.evaluate(attachmentFile, context, message)
                .orElseThrow(() -> new MailAttachmentException(ATTACHMENT_FILE_EMPTY.format(attachmentFile.toString())));

        Path attachmentFilePath = Paths.get(filePathAndName);

        String finalFileName = attachmentFileNameFrom(scriptEngine, context, message, userDefinedFileName, attachmentFilePath);

        try {

            byte[] data = Files.readAllBytes(attachmentFilePath);

            String attachmentContentType = ContentType.from(contentType, charset);

            ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);

            email.attach(dataSource, finalFileName, name);

        } catch (IOException | EmailException exception) {
            throw new MailAttachmentException(exception.getMessage(), exception);
        }
    }
}
