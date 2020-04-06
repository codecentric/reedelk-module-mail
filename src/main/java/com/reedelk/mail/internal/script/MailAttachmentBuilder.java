package com.reedelk.mail.internal.script;

import com.reedelk.runtime.api.annotation.AutocompleteItem;
import com.reedelk.runtime.api.annotation.AutocompleteType;

@AutocompleteType(
        global = true,
        description = "The MailAttachmentBuilder creates new Mail Attachment objects.")
public class MailAttachmentBuilder {

    @AutocompleteItem(signature = "create()",
            example = "MailAttachmentBuilder.create()",
            description = "Creates a new MailAttachment object.")
    public MailAttachment create() {
        return new MailAttachment();
    }
}
