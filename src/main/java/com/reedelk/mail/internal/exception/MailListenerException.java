package com.reedelk.mail.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class MailListenerException extends PlatformException {

    public MailListenerException(String message) {
        super(message);
    }

    public MailListenerException(String message, Exception original) {
        super(message, original);
    }
}
