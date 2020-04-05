package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public interface AttachmentSourceStrategy {

    default MimeBodyPart build(ScriptEngineService scriptEngine,
                               AttachmentDefinition definition,
                               FlowContext context,
                               Message message) throws MessagingException {
        throw new UnsupportedOperationException("Operation not supported for type");
    }

    default MimeBodyPart build(ScriptEngineService scriptEngine,
                               ConverterService converterService,
                               String attachmentName,
                               Attachment attachment) {
        throw new UnsupportedOperationException("Operation not supported for type");
    }
}
