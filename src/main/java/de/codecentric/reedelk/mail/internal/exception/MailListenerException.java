package de.codecentric.reedelk.mail.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class MailListenerException extends PlatformException {

    public MailListenerException(String message) {
        super(message);
    }

    public MailListenerException(String message, Exception original) {
        super(message, original);
    }
}
