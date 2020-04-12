package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class MailAttachmentException extends PlatformException {

    public MailAttachmentException(String message) {
        super(message);
    }

    public MailAttachmentException(String message, Exception original) {
        super(message, original);
    }
}
