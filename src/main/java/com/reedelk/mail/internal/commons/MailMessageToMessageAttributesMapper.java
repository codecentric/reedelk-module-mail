package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.send.MailSendAttributes;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.message.DefaultMessageAttributes;
import org.apache.commons.mail.Email;

import javax.mail.MessagingException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MailMessageToMessageAttributesMapper {

    public static com.reedelk.runtime.api.message.MessageAttributes from(Class<? extends Component> componentClazz,
                                                                         Email mail) throws MessagingException {
        Map<String, Serializable> attributesMap = baseAttributesMap(mail);
        return new DefaultMessageAttributes(componentClazz, attributesMap);
    }

    private static Map<String, Serializable> baseAttributesMap(Email mail) {
        Map<String, Serializable> attributesMap = new HashMap<>();
        MailSendAttributes.SUBJECT.set(attributesMap, mail.getSubject());
        MailSendAttributes.SENT_DATE.set(attributesMap, mail.getSentDate().getTime());
        MailSendAttributes.MESSAGE_NUMBER.set(attributesMap, mail.getMimeMessage().getMessageNumber());
        MailSendAttributes.FROM.set(attributesMap, mail.getFromAddress().toString());
        MailSendAttributes.REPLY_TO.set(attributesMap, Address.asSerializableList(mail.getReplyToAddresses()));
        MailSendAttributes.TO.set(attributesMap, Address.asSerializableList(mail.getToAddresses()));
        MailSendAttributes.CC.set(attributesMap, Address.asSerializableList(mail.getCcAddresses()));
        MailSendAttributes.BCC.set(attributesMap, Address.asSerializableList(mail.getBccAddresses()));
        return attributesMap;
    }
}
