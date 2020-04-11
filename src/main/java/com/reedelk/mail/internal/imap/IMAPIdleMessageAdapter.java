package com.reedelk.mail.internal.imap;

import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.internal.commons.OnMessageEvent;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.sun.mail.imap.IMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Arrays;

public class IMAPIdleMessageAdapter extends MessageCountAdapter {

    private final Logger logger = LoggerFactory.getLogger(IMAPIdleMessageAdapter.class);

    private final IMAPIdleListenerSettings settings;
    private final InboundEventListener listener;

    public IMAPIdleMessageAdapter(InboundEventListener listener, IMAPIdleListenerSettings settings) {
        this.settings = settings;
        this.listener = listener;
    }

    @Override
    public void messagesAdded(MessageCountEvent event) {
        Message[] mails = event.getMessages();

        boolean peek = settings.isPeek();
        if (settings.isBatch()) {
            if (peek) {
                Arrays.stream(mails).forEach(mail -> ((IMAPMessage) mail).setPeek(peek));
            }

            try {
                if (OnMessageEvent.fire(IMAPMailListener.class, listener, mails)) {
                    applyMessagesOnSuccessFlags(mails);
                } else {
                    applyMessagesOnFailureFlags(mails);
                }
            } catch (Exception exception) {
                String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                logger.error(error, exception);
            }

        } else {
            for (Message message : mails) {

                if (peek) {
                    // If peek == true, the message is not marked as 'seen' after it has been processed.
                    // otherwise the message is flagged as 'seen' after the flow consumed it.
                    ((IMAPMessage) message).setPeek(peek);
                }

                try {
                    if (OnMessageEvent.fire(IMAPMailListener.class, listener, message)) {
                        applyMessageOnSuccessFlags(message);
                    } else {
                        applyMessageOnFailureFlags(message);
                    }
                } catch (Exception exception) {
                    String error = String.format("Could not map IMAP Message=[%s]", exception.getMessage());
                    logger.error(error, exception);
                }
            }
        }
    }

    private void applyMessagesOnFailureFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessageOnSuccessFlags(Message message) throws MessagingException {
        if (settings.isDeleteOnSuccess()) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    // If failure, we don't wat to mark the message as 'seen'
    private void applyMessageOnFailureFlags(Message message) throws MessagingException {
        message.setFlag(Flags.Flag.SEEN, false);
    }
}
