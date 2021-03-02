package de.codecentric.reedelk.mail.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class MailAttachmentException extends PlatformException {

    public MailAttachmentException(String message) {
        super(message);
    }

    public MailAttachmentException(String message, Exception original) {
        super(message, original);
    }
}
