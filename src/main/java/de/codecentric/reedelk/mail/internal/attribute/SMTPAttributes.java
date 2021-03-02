package de.codecentric.reedelk.mail.internal.attribute;

import de.codecentric.reedelk.runtime.api.annotation.Type;
import de.codecentric.reedelk.runtime.api.annotation.TypeProperty;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.type.ListOfString;
import org.apache.commons.mail.Email;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

import static de.codecentric.reedelk.mail.internal.attribute.SMTPAttributes.*;
import static de.codecentric.reedelk.runtime.api.commons.SerializableUtils.asSerializableList;
import static java.util.stream.Collectors.toList;

@Type
@TypeProperty(name = FROM, type = String.class)
@TypeProperty(name = TO, type = ListOfString.class)
@TypeProperty(name = CC, type = ListOfString.class)
@TypeProperty(name = BCC, type = ListOfString.class)
@TypeProperty(name = SUBJECT, type = String.class)
@TypeProperty(name = REPLY_TO, type = ListOfString.class)
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
        put(FROM, mail.getFromAddress().toUnicodeString());
        put(REPLY_TO, asList(mail.getReplyToAddresses()));
        put(TO, asList(mail.getToAddresses()));
        put(CC, asList(mail.getCcAddresses()));
        put(BCC, asList(mail.getBccAddresses()));
    }

    private ArrayList<String> asList(List<InternetAddress> addresses) {
        if (addresses == null) return new ArrayList<>();
        return asSerializableList(addresses.stream()
                .map(InternetAddress::toUnicodeString)
                .collect(toList()));
    }
}
