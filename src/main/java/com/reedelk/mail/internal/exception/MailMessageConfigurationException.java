package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class MailMessageConfigurationException extends ESBException {

    public MailMessageConfigurationException(String message, Exception original) {
        super(message, original);
    }

    public MailMessageConfigurationException(String message) {
        super(message);
    }
}
