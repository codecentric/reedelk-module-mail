package de.codecentric.reedelk.mail.component.smtp;

import de.codecentric.reedelk.runtime.api.annotation.DisplayName;

public enum AttachmentSourceType {
    @DisplayName("File")
    FILE,
    @DisplayName("Project Resource")
    RESOURCE,
    @DisplayName("Expression")
    EXPRESSION
}
