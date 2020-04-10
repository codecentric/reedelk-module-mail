package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.mail.internal.send.attachment.AttachmentSourceStrategyFactory;
import com.reedelk.runtime.api.commons.Unchecked;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import org.apache.commons.mail.MultiPartEmail;

import java.util.List;
import java.util.Map;

import static com.reedelk.runtime.api.commons.DynamicValueUtils.isNotNullOrBlank;
import static com.reedelk.runtime.api.commons.Preconditions.checkArgument;

public class MailAttachmentBuilder {

    private MultiPartEmail email;
    private Message message;
    private FlowContext context;
    private DynamicObject attachmentsMap;
    private ScriptEngineService scriptEngine;
    private ConverterService converterService;
    private List<AttachmentDefinition> attachments;

    private MailAttachmentBuilder(MultiPartEmail email) {
        this.email = email;
    }

    public static MailAttachmentBuilder get(MultiPartEmail email) {
        return new MailAttachmentBuilder(email);
    }

    public MailAttachmentBuilder message(Message message) {
        this.message = message;
        return this;
    }

    public MailAttachmentBuilder context(FlowContext context) {
        this.context = context;
        return this;
    }

    public MailAttachmentBuilder converter(ConverterService converterService) {
        this.converterService = converterService;
        return this;
    }

    public MailAttachmentBuilder scriptEngine(ScriptEngineService scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    public MailAttachmentBuilder attachmentsMap(DynamicObject attachmentsObject) {
        this.attachmentsMap = attachmentsObject;
        return this;
    }

    public MailAttachmentBuilder attachments(List<AttachmentDefinition> attachments) {
        this.attachments = attachments;
        return this;
    }

    public void build() {
        if (isNotNullOrBlank(attachmentsMap)) {
            buildFromAttachmentsMap();
        }

        attachments.forEach(Unchecked.consumer(definition ->
                AttachmentSourceStrategyFactory
                        .from(definition)
                        .build(scriptEngine, definition, email, context, message)));
    }

    @SuppressWarnings("unchecked")
    private void buildFromAttachmentsMap() {
        Object evaluationResult = scriptEngine.evaluate(attachmentsMap, context, message).orElse(null);

        if (evaluationResult == null) return;

        // The evaluated result must be an instance of attachments.
        checkArgument(Attachment.isAttachmentMap(evaluationResult), "Expected Attachments Objects");


        Map<String, Attachment> attachments = (Map<String, Attachment>) evaluationResult;
        attachments.forEach((attachmentName, attachment) -> {
            AttachmentSourceStrategyFactory
                    .fromAttachment()
                    .build(scriptEngine, converterService, email, attachmentName, attachment);
        });
    }
}
