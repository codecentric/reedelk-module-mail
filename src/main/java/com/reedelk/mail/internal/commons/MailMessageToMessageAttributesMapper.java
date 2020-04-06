package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.send.MailSendAttributes;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.message.DefaultMessageAttributes;
import com.reedelk.runtime.api.message.content.Attachment;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static javax.mail.Message.RecipientType.*;

public class MailMessageToMessageAttributesMapper {

    public static com.reedelk.runtime.api.message.MessageAttributes from(Class<? extends Component> componentClazz,
                                                                         javax.mail.Message mail) throws MessagingException {
        Map<String, Serializable> attributesMap = baseAttributesMap(mail);
        return new DefaultMessageAttributes(componentClazz, attributesMap);
    }

    public static com.reedelk.runtime.api.message.MessageAttributes from(
            Class<? extends Component> componentClazz,
            javax.mail.Message mail,
            ArrayList<Attachment> attachments) throws MessagingException {
        Map<String, Serializable> attributesMap = baseAttributesMap(mail);
        MailSendAttributes.ATTACHMENTS.set(attributesMap, attachments);
        return new DefaultMessageAttributes(componentClazz, attributesMap);
    }

    private static Map<String, Serializable> baseAttributesMap(Message mail) throws MessagingException {
        Map<String, Serializable> attributesMap = new HashMap<>();
        MailSendAttributes.SUBJECT.set(attributesMap, mail.getSubject());
        MailSendAttributes.SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
        MailSendAttributes.MESSAGE_NUMBER.set(attributesMap, mail.getMessageNumber());
        MailSendAttributes.FROM.set(attributesMap, Address.asSerializableList(mail.getFrom()));
        MailSendAttributes.REPLY_TO.set(attributesMap, Address.asSerializableList(mail.getReplyTo()));
        MailSendAttributes.TO.set(attributesMap, Address.asSerializableList(mail.getRecipients(TO)));
        MailSendAttributes.CC.set(attributesMap, Address.asSerializableList(mail.getRecipients(CC)));
        MailSendAttributes.BCC.set(attributesMap, Address.asSerializableList(mail.getRecipients(BCC)));
        return attributesMap;
    }
}
