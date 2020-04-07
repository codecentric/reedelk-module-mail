package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.MailListener;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

public class ImapIdleMessageListener extends MessageCountAdapter {

    public static final Logger logger = LoggerFactory.getLogger(ImapIdleMessageListener.class);

    private final InboundEventListener listener;

    public ImapIdleMessageListener(InboundEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void messagesAdded(MessageCountEvent event) {
        Message[] messages = event.getMessages();

        for (Message message : messages) {
            try {
                // TODO: The message must be set to deleted.
                com.reedelk.runtime.api.message.Message inMessage =
                        MailMessageToMessageMapper.map(MailListener.class, message);
                listener.onEvent(inMessage);
            } catch (Exception exception) {
                String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                logger.error(error, exception);
            }
        }
    }
}
