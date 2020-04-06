package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;

public class MailMessageToMessageMapper {

    public static Message map(javax.mail.Message mailMessage) {
        return MessageBuilder.get().empty().build();
    }
}
