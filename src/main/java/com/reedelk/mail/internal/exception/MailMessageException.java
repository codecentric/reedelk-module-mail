package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class MailMessageException extends ESBException {

    public MailMessageException(String message, Exception original) {
        super(message, original);
    }
}
