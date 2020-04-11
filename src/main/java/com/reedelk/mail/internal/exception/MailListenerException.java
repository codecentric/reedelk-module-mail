package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class MailListenerException extends ESBException {

    public MailListenerException(String message) {
        super(message);
    }

    public MailListenerException(String message, Exception original) {
        super(message, original);
    }
}
