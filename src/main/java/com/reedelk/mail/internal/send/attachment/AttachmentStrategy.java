package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.component.AttachmentSourceType;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.exception.ESBException;

import java.util.Map;

public class AttachmentStrategy {

    private static final Map<AttachmentSourceType, Strategy> STRATEGY_MAP = ImmutableMap.of(
            AttachmentSourceType.FILE, new FileType(),
            AttachmentSourceType.EXPRESSION, new ExpressionType(),
            AttachmentSourceType.RESOURCE, new ResourceType());

    public static Strategy from(AttachmentDefinition definition) {
        if (STRATEGY_MAP.containsKey(definition.getAttachmentSourceType())) {
            return STRATEGY_MAP.get(definition.getAttachmentSourceType());
        }
        throw new ESBException("Strategy not found");
    }
}
