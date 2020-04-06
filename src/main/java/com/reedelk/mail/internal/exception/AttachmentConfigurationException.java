package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class AttachmentConfigurationException extends ESBException {

    public AttachmentConfigurationException(String message) {
        super(message);
    }

    public AttachmentConfigurationException(String message, Exception original) {
        super(message, original);
    }
}
