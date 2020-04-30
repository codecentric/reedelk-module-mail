package com.reedelk.mail.internal.attribute;

import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.apache.commons.mail.Email;

import java.util.List;

import static com.reedelk.mail.internal.attribute.SMTPAttributes.*;
import static com.reedelk.runtime.api.commons.SerializableUtils.asSerializableList;

@Type
@TypeProperty(name = FROM, type = String.class)
@TypeProperty(name = TO, type = List.class)
@TypeProperty(name = CC, type = List.class)
@TypeProperty(name = BCC, type = List.class)
@TypeProperty(name = SUBJECT, type = String.class)
@TypeProperty(name = REPLY_TO, type = List.class)
@TypeProperty(name = SENT_DATE, type = long.class)
@TypeProperty(name = MESSAGE_NUMBER, type = int.class)
public class SMTPAttributes extends MessageAttributes {

    static final String FROM = "from";
    static final String TO = "to";
    static final String CC = "cc";
    static final String BCC = "bcc";
    static final String SUBJECT = "subject";
    static final String REPLY_TO = "replyTo";
    static final String SENT_DATE = "sentDate";
    static final String MESSAGE_NUMBER = "messageNumber";

    public SMTPAttributes(Email mail) {
        put(SUBJECT, mail.getSubject());
        put(SENT_DATE, mail.getSentDate().getTime());
        put(MESSAGE_NUMBER, mail.getMimeMessage().getMessageNumber());
        put(FROM, mail.getFromAddress().toString());
        put(REPLY_TO, asSerializableList(mail.getReplyToAddresses()));
        put(TO, asSerializableList(mail.getToAddresses()));
        put(CC, asSerializableList(mail.getCcAddresses()));
        put(BCC, asSerializableList(mail.getBccAddresses()));
    }
}
