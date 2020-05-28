package com.reedelk.mail.internal.type;

import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.type.MapOfAttachments;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Message;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static com.reedelk.mail.internal.type.MailMessage.*;
import static com.reedelk.runtime.api.commons.SerializableUtils.asSerializableList;

@Type(displayName = "MailMessage", mapKeyType = String.class, mapValueType = Serializable.class)
@TypeProperty(name = FROM, type = String.class)
@TypeProperty(name = TO, type = List.class)
@TypeProperty(name = CC, type = List.class)
@TypeProperty(name = BCC, type = List.class)
@TypeProperty(name = BODY, type = String.class)
@TypeProperty(name = BODY_MIME_TYPE, type = MimeType.class)
@TypeProperty(name = SUBJECT, type = String.class)
@TypeProperty(name = REPLY_TO, type = String.class)
@TypeProperty(name = SENT_DATE, type = long.class)
@TypeProperty(name = RECEIVED_DATE, type = long.class)
@TypeProperty(name = ATTACHMENTS, type = MapOfAttachments.class)
@TypeProperty(name = MailMessage.MESSAGE_NUMBER, type = int.class)
public class MailMessage extends HashMap<String, Serializable> {

    static final String FROM = "from";
    static final String TO = "to";
    static final String CC = "cc";
    static final String BCC = "bcc";
    static final String BODY = "body";
    static final String BODY_MIME_TYPE = "bodyMimeType";
    static final String SUBJECT = "subject";
    static final String REPLY_TO = "replyTo";
    static final String SENT_DATE = "sentDate";
    static final String RECEIVED_DATE = "receivedDate";
    static final String ATTACHMENTS = "attachments";
    static final String MESSAGE_NUMBER = "messageNumber";

    public MailMessage(Message mail, MimeMessageParser parsed, HashMap<String, Attachment> attachments) throws Exception {
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

        if (parsed.hasHtmlContent()) {
            put(BODY, parsed.getHtmlContent());
            put(BODY_MIME_TYPE, MimeType.TEXT_HTML);
        } else if (parsed.hasPlainContent()) {
            put(BODY, parsed.getPlainContent());
            put(BODY_MIME_TYPE, MimeType.TEXT_PLAIN);
        }
    }
}
