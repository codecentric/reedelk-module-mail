package com.reedelk.mail.internal.smtp.attachment;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.exception.AttachmentConfigurationException;
import com.reedelk.runtime.api.commons.StreamUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.resource.ResourceBinary;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.util.ByteArrayDataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_RESOURCE_MUST_NOT_BE_EMPTY;

public class ResourceType extends AbstractAttachment {

    @Override
    public void build(ScriptEngineService scriptEngine,
                      AttachmentDefinition definition,
                      MultiPartEmail email,
                      FlowContext context,
                      Message message) {

        String name = definition.getName();
        String charset = definition.getCharset();
        String contentType = definition.getContentType();
        DynamicString userDefinedFileName = definition.getFileName();

        ResourceBinary resourceFile = Optional.ofNullable(definition.getResourceFile())
                .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_RESOURCE_MUST_NOT_BE_EMPTY.format()));

        Path theResourceFilePath = Paths.get(resourceFile.path());

        String finalFileName = attachmentFileNameFrom(scriptEngine, context, message, userDefinedFileName, theResourceFilePath);

        try {

            byte[] data = StreamUtils.FromByteArray.consume(resourceFile.data());

            String attachmentContentType = ContentType.from(contentType, charset);

            ByteArrayDataSource dataSource = new ByteArrayDataSource(data, attachmentContentType);

            email.attach(dataSource, finalFileName, name);

        } catch (EmailException exception) {
            throw new AttachmentConfigurationException(exception.getMessage(), exception);
        }
    }
}
