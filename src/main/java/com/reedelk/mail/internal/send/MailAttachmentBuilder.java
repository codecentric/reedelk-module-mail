package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.send.attachment.AttachmentSourceStrategyFactory;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Attachments;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.List;

import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;
import static com.reedelk.runtime.api.commons.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

public class MailAttachmentBuilder {

    private Message message;
    private FlowContext context;
    private DynamicObject attachmentsObject;
    private ScriptEngineService scriptEngine;
    private ConverterService converterService;
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

    public MailAttachmentBuilder withConverter(ConverterService converterService) {
        this.converterService = converterService;
        return this;
    }

    public MailAttachmentBuilder withScriptEngine(ScriptEngineService scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    public MailAttachmentBuilder withAttachmentsObject(DynamicObject attachmentsObject) {
        this.attachmentsObject = attachmentsObject;
        return this;
    }

    public MailAttachmentBuilder withAttachments(List<AttachmentDefinition> attachments) {
        this.attachments = attachments;
        return this;
    }

    public void build(Multipart multipart) {
        if (isNotNullOrBlank(attachmentsObject)) {
            fromAttachmentObject().forEach(mimeBodyPart -> addPart(multipart, mimeBodyPart));
        }
        fromAttachmentDefinitions(multipart).forEach(mimeBodyPart -> addPart(multipart, mimeBodyPart));
    }

    private List<MimeBodyPart> fromAttachmentObject() {
        Object evaluationResult = scriptEngine.evaluate(attachmentsObject, context, message)
                .orElseThrow(() -> { throw new ESBException("Error"); });

        // The evaluated result must be an instance of attachments.
        checkArgument(evaluationResult instanceof Attachments,"Expected Attachments Objects");

        List<MimeBodyPart> parts = new ArrayList<>();
        Attachments attachments = (Attachments) evaluationResult;
        attachments.forEach((attachmentName, attachment) -> {
            MimeBodyPart part = AttachmentSourceStrategyFactory.fromAttachment()
                    .build(scriptEngine, converterService, attachmentName, attachment);
            parts.add(part);
        });

        return parts;
    }

    private List<MimeBodyPart> fromAttachmentDefinitions(Multipart multipart) {
        return attachments.stream().map(attachmentDefinition ->
                AttachmentSourceStrategyFactory.from(attachmentDefinition)
                        .build(scriptEngine, attachmentDefinition, context, message))
                .collect(toList());
    }

    private void addPart(Multipart multipart, MimeBodyPart mimeBodyPart) {
        try {
            multipart.addBodyPart(mimeBodyPart);
        } catch (MessagingException e) {
            throw new ESBException(e);
        }
    }
}
