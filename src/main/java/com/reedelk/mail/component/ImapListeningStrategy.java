package com.reedelk.mail.component;

import com.reedelk.runtime.api.annotation.DisplayName;

public enum ImapListeningStrategy {
    @DisplayName("Idle (imap4 IDLE command - RFC 2177)")
    IDLE,
    @DisplayName("Polling")
    POLLING
}
