package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

abstract class AbstractAttachmentSourceStrategy implements AttachmentSourceStrategy {

    @Override
    public MimeBodyPart build(ScriptEngineService scriptEngine,
                                        AttachmentDefinition definition,
                                        FlowContext context,
                                        Message message) {
        try {
            return buildInternal(scriptEngine, definition, context, message);
        } catch (Exception exception) {
            throw new ESBException(exception);
        }
    }

    abstract MimeBodyPart buildInternal(ScriptEngineService scriptEngine,
                                                  AttachmentDefinition definition,
                                                  FlowContext context,
                                                  Message message) throws MessagingException;
}
