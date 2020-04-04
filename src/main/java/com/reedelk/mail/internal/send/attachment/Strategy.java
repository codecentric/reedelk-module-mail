package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public interface Strategy {

    MimeBodyPart attach(ScriptEngineService scriptEngine,
                        AttachmentDefinition definition,
                        FlowContext context,
                        Message message) throws MessagingException;
}
