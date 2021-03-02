package de.codecentric.reedelk.mail.component.imap;

import de.codecentric.reedelk.runtime.api.annotation.DisplayName;

public enum IMAPListeningStrategy {
    @DisplayName("Idle (imap4 IDLE command - RFC 2177)")
    IDLE,
    @DisplayName("Polling")
    POLLING
}
