package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.exception.AttachmentConfigurationException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicByteArray;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.util.ByteArrayDataSource;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNullOrBlank;

public class ExpressionType implements AttachmentSourceStrategy {

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
        DynamicByteArray contentExpression = definition.getExpression();

        if (isNullOrBlank(userDefinedFileName)) {
            throw new AttachmentConfigurationException(ATTACHMENT_FILE_NAME.format(userDefinedFileName.toString()));
        }

        // The file name is mandatory, otherwise the attachment cannot be sent.
        String finalFileName = scriptEngine.evaluate(userDefinedFileName, context, message)
                .orElseThrow(() -> new AttachmentConfigurationException(ATTACHMENT_FILE_NAME.format(userDefinedFileName.toString())));

        try {

            // We accept the fact that we can send an empty file.
            String attachmentContentType = ContentType.from(contentType, charset);

            ByteArrayDataSource dataSource = scriptEngine.evaluate(contentExpression, context, message)
                    .map(bytes -> new ByteArrayDataSource(bytes, attachmentContentType))
                    .orElse(new ByteArrayDataSource(new byte[0], attachmentContentType));

            email.attach(dataSource, finalFileName, name);

        } catch (EmailException exception) {
            throw new AttachmentConfigurationException(exception.getMessage(), exception);
        }
    }
}
