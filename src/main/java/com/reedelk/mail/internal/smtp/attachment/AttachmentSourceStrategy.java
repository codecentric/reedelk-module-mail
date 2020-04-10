package com.reedelk.mail.internal.smtp.attachment;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.script.ScriptEngineService;
import org.apache.commons.mail.MultiPartEmail;

public interface AttachmentSourceStrategy {

    default void build(ScriptEngineService scriptEngine,
                       AttachmentDefinition definition,
                       MultiPartEmail email,
                       FlowContext context,
                       Message message) {
        throw new UnsupportedOperationException("Operation not supported for type");
    }

    default void build(ScriptEngineService scriptEngine,
                       ConverterService converterService,
                       MultiPartEmail email,
                       String attachmentName,
                       Attachment attachment) {
        throw new UnsupportedOperationException("Operation not supported for type");
    }
}
