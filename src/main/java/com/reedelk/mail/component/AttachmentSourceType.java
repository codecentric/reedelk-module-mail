package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.DisplayName;

public enum AttachmentSourceType {

    @DisplayName("File")
    FILE,
    @DisplayName("Project Resource")
    RESOURCE,
    @DisplayName("Expression")
    EXPRESSION
}
