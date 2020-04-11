package com.reedelk.mail.internal.smtp.attachment;

import com.reedelk.mail.internal.exception.MailAttachmentException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

import java.nio.file.Path;

import static com.reedelk.mail.internal.commons.Messages.MailSendComponent.ATTACHMENT_FILE_NAME_EMPTY;
import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;

abstract class AbstractAttachment implements AttachmentSourceStrategy {

    protected String attachmentFileNameFrom(ScriptEngineService scriptEngine,
                                          FlowContext context,
                                          Message message,
                                          DynamicString userDefinedFileName,
                                          Path attachmentFilePath) {
        return isNotNullOrBlank(userDefinedFileName) ?
                // We take the final file name from the file name field if the user defined it.
                scriptEngine.evaluate(userDefinedFileName, context, message)
                        .orElseThrow(() -> new MailAttachmentException(ATTACHMENT_FILE_NAME_EMPTY.format(userDefinedFileName.toString()))) :
                // Otherwise the file name from the file path.
                attachmentFilePath.getFileName().toString();
    }
}
