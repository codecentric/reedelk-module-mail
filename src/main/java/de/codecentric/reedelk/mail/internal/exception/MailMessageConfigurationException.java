package de.codecentric.reedelk.mail.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class MailMessageConfigurationException extends PlatformException {

    public MailMessageConfigurationException(String message, Exception original) {
        super(message, original);
    }

    public MailMessageConfigurationException(String message) {
        super(message);
    }
}
