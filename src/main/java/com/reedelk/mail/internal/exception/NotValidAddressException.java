package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class NotValidAddressException extends ESBException {

    public NotValidAddressException(Exception original, String message) {
        super(message, original);
    }

    public NotValidAddressException(String message) {
        super(message);
    }
}
