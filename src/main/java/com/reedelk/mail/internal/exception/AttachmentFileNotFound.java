package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.ESBException;

public class AttachmentFileNotFound extends ESBException {

    public AttachmentFileNotFound(String message) {
        super(message);
    }
}
