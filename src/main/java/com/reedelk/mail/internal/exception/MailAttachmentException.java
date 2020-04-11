package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class MailAttachmentException extends ESBException {

    public MailAttachmentException(String message) {
        super(message);
    }

    public MailAttachmentException(String message, Exception original) {
        super(message, original);
    }
}
