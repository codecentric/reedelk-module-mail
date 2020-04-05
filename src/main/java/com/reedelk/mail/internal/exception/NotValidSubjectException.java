package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class NotValidSubjectException extends ESBException {

    public NotValidSubjectException(Exception original, String message) {
        super(message, original);
    }
}
