package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.MailSend;
import com.reedelk.mail.internal.commons.Address;
import com.reedelk.runtime.api.message.DefaultMessageAttributes;

import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static javax.mail.Message.RecipientType.*;

public class MessageAttributesMapper {

    public static com.reedelk.runtime.api.message.MessageAttributes from(javax.mail.Message mail) throws MessagingException {
        Map<String, Serializable> attributesMap = new HashMap<>();
        MailSendAttributes.SUBJECT.set(attributesMap, mail.getSubject());
        MailSendAttributes.SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
        MailSendAttributes.FROM.set(attributesMap, Address.asSerializableList(mail.getFrom()));
        MailSendAttributes.REPLY_TO.set(attributesMap, Address.asSerializableList(mail.getReplyTo()));
        MailSendAttributes.TO.set(attributesMap, Address.asSerializableList(mail.getRecipients(TO)));
        MailSendAttributes.CC.set(attributesMap, Address.asSerializableList(mail.getRecipients(CC)));
        MailSendAttributes.BCC.set(attributesMap, Address.asSerializableList(mail.getRecipients(BCC)));
        return new DefaultMessageAttributes(MailSend.class, attributesMap);
    }
}
