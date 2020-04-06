package com.reedelk.mail.internal.listener;

import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

public class IMAPMessageListener extends MessageCountAdapter {

    private final InboundEventListener listener;

    public IMAPMessageListener(InboundEventListener listener) {
        this.listener = listener;
    }
    @Override
    public void messagesAdded(MessageCountEvent event) {
        Message[] messages = event.getMessages();

        for (Message message : messages) {
            try {
                com.reedelk.runtime.api.message.Message inMessage =
                        MailMessageToMessageMapper.map(message);
                listener.onEvent(inMessage);

                System.out.println("Mail Subject:- " + message.getSubject());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
