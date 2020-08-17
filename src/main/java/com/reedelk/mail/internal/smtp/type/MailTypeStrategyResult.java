package com.reedelk.mail.internal.smtp.type;

import com.reedelk.runtime.api.message.content.MimeType;
import org.apache.commons.mail.Email;

public class MailTypeStrategyResult {

    public final Email email;
    public final String text;
    public final MimeType mimeType;

    private MailTypeStrategyResult(Email email, String text, MimeType mimeType) {
        this.mimeType = mimeType;
        this.email = email;
        this.text = text;
    }

    public static MailTypeStrategyResult create(Email email, String text, MimeType mimeType) {
        return new MailTypeStrategyResult(email, text, mimeType);
    }
}
