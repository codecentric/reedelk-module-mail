package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class NotValidBodyException extends ESBException {

    public NotValidBodyException(String message, Exception original) {
        super(message, original);
    }
}
