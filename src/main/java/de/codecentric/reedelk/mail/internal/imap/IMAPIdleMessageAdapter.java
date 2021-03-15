package de.codecentric.reedelk.mail.internal.imap;

import de.codecentric.reedelk.mail.component.IMAPMailListener;
import de.codecentric.reedelk.mail.internal.commons.FlagUtils;
import de.codecentric.reedelk.mail.internal.commons.OnMessageEvent;
import de.codecentric.reedelk.runtime.api.component.InboundEventListener;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Arrays;

public class IMAPIdleMessageAdapter extends MessageCountAdapter {

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

            if (OnMessageEvent.fire(IMAPMailListener.class, listener, mails)) {
                applyMessagesOnSuccessFlags(mails);
            } else {
                applyMessagesOnFailureFlags(mails);
            }

        } else {
            for (Message message : mails) {
                // If peek == true, the message is not marked as 'seen' after it has been processed.
                // otherwise the message is flagged as 'seen' after the flow consumed it.
                if (peek) {
                    ((IMAPMessage) message).setPeek(peek);
                }

                if (OnMessageEvent.fire(IMAPMailListener.class, listener, message)) {
                    applyMessageOnSuccessFlags(message);
                } else {
                    applyMessageOnFailureFlags(message);
                }
            }
        }
    }

    private void applyMessagesOnFailureFlags(Message[] messages) {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private void applyMessageOnSuccessFlags(Message message) {
        if (settings.isDeleteOnSuccess()) {
            FlagUtils.deleted(message);
        }
    }

    private void applyMessageOnFailureFlags(Message message) {
        // If failure, we don't wat to mark the message as 'seen'
        FlagUtils.notSeen(message);
    }
}
