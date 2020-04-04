package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.send.attachment.AttachmentStrategy;
import com.reedelk.runtime.api.commons.DynamicValueUtils;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.Attachments;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MailAttachmentBuilder {

    private Message message;
    private FlowContext context;
    private DynamicObject attachmentsObject;
    private ScriptEngineService scriptEngine;
    private List<AttachmentDefinition> attachments;

    private MailAttachmentBuilder() {
    }

    public static MailAttachmentBuilder get() {
        return new MailAttachmentBuilder();
    }

    public MailAttachmentBuilder withMessage(Message message) {
        this.message = message;
        return this;
    }

    public MailAttachmentBuilder withFlowContext(FlowContext context) {
        this.context = context;
        return this;
    }

    public MailAttachmentBuilder withAttachmentsObject(DynamicObject attachmentsObject) {
        this.attachmentsObject = attachmentsObject;
        return this;
    }

    public MailAttachmentBuilder withScriptEngine(ScriptEngineService scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    public MailAttachmentBuilder withAttachments(List<AttachmentDefinition> attachments) {
        this.attachments = attachments;
        return this;
    }

    public void build(Multipart multipart) {
        if (DynamicValueUtils.isNotNullOrBlank(attachmentsObject)) {
            scriptEngine.evaluate(attachmentsObject, context, message).ifPresent(new Consumer<Object>() {
                @Override
                public void accept(Object evaluationResult) {
                    // The evaluated result must be an instance of attachments.
                    if (!(evaluationResult instanceof Attachments)) {
                        throw new ESBException("Expected mail attachments object");
                    }
                    Attachments attachments = (Attachments) evaluationResult;
                    attachments.forEach(new BiConsumer<String, Attachment>() {
                        @Override
                        public void accept(String attachmentName, Attachment attachment) {
                            // Convert data to bytes?
                            Object data = attachment.content().data();
                            // TODO: Finish this and create a Mail Multipart Builder.
                        }
                    });
                }
            });
        }

        attachments.forEach(attachmentDefinition -> {
            try {
                AttachmentStrategy.from(attachmentDefinition)
                        .attach(scriptEngine, attachmentDefinition, context, message)
                        .ifPresent(mimeBodyPart -> {
                            try {
                                multipart.addBodyPart(mimeBodyPart);
                            } catch (MessagingException e) {
                                throw new ESBException(e);
                            }
                        });
            } catch (MessagingException e) {
                throw new ESBException(e);
            }
        });
    }
}
