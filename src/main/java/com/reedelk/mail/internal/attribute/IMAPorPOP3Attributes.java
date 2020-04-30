package com.reedelk.mail.internal.attribute;

import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.content.Attachment;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.mail.internal.attribute.IMAPorPOP3Attributes.*;
import static com.reedelk.runtime.api.commons.SerializableUtils.asSerializableList;

@Type
@TypeProperty(name = FROM, type = String.class)
@TypeProperty(name = TO, type = List.class)
@TypeProperty(name = CC, type = List.class)
@TypeProperty(name = BCC, type = List.class)
@TypeProperty(name = SUBJECT, type = String.class)
@TypeProperty(name = REPLY_TO, type = String.class)
@TypeProperty(name = SENT_DATE, type = long.class)
@TypeProperty(name = RECEIVED_DATE, type = long.class)
@TypeProperty(name = ATTACHMENTS, type = Map.class)
@TypeProperty(name = MESSAGE_NUMBER, type = int.class)
public class IMAPorPOP3Attributes extends MessageAttributes {

    static final String FROM = "from";
    static final String TO = "to";
    static final String CC = "cc";
    static final String BCC = "bcc";
    static final String SUBJECT = "subject";
    static final String REPLY_TO = "replyTo";
    static final String SENT_DATE = "sentDate";
    static final String RECEIVED_DATE = "receivedDate";
    static final String ATTACHMENTS = "attachments";
    static final String MESSAGE_NUMBER = "messageNumber";

    public IMAPorPOP3Attributes(Message mail, MimeMessageParser parsed, HashMap<String, Attachment> attachments) throws Exception {
        put(FROM, parsed.getFrom());
        put(SUBJECT, parsed.getSubject());
        put(REPLY_TO, parsed.getReplyTo());
        put(TO, asSerializableList(parsed.getTo()));
        put(CC, asSerializableList(parsed.getCc()));
        put(BCC, asSerializableList(parsed.getBcc()));
        put(ATTACHMENTS, attachments);
        put(MESSAGE_NUMBER, mail.getMessageNumber());
        if (mail.getSentDate() != null) put(SENT_DATE, mail.getSentDate().getTime());
        if (mail.getReceivedDate() != null) put(RECEIVED_DATE, mail.getReceivedDate().getTime());
    }
}
