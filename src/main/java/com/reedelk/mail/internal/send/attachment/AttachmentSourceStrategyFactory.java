package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.smtp.AttachmentDefinition;
import com.reedelk.mail.component.smtp.AttachmentSourceType;

import java.util.Map;

import static com.reedelk.runtime.api.commons.ImmutableMap.of;

public class AttachmentSourceStrategyFactory {

    private static final Map<AttachmentSourceType, AttachmentSourceStrategy> STRATEGY_MAP =
            of(AttachmentSourceType.FILE, new FileType(),
                    AttachmentSourceType.EXPRESSION, new ExpressionType(),
                    AttachmentSourceType.RESOURCE, new ResourceType());

    private static final AttachmentSourceStrategy FROM_ATTACHMENT = new AttachmentObjectType();

    public static AttachmentSourceStrategy from(AttachmentDefinition definition) {
        if (STRATEGY_MAP.containsKey(definition.getSourceType())) {
            return STRATEGY_MAP.get(definition.getSourceType());
        }
        return STRATEGY_MAP.get(AttachmentSourceType.RESOURCE); // Default strategy.
    }

    public static AttachmentSourceStrategy fromAttachment() {
        return FROM_ATTACHMENT;
    }
}
