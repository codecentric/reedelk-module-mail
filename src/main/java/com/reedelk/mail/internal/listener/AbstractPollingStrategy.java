package com.reedelk.mail.internal.listener;

import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Message;
import java.util.Optional;

public abstract class AbstractPollingStrategy implements PollingStrategy {

    protected final InboundEventListener listener;
    protected final Boolean deleteAfterRetrieve;

    public AbstractPollingStrategy(InboundEventListener listener, Boolean deleteAfterRetrieve) {
        this.listener = listener;
        this.deleteAfterRetrieve = Optional.ofNullable(deleteAfterRetrieve).orElse(false);
    }

    protected boolean processMessage(Message message) throws Exception {
        com.reedelk.runtime.api.message.Message inMessage =
                MailMessageToMessageMapper.map(IMAPMailListener.class, message);
        this.listener.onEvent(inMessage);
        // TODO: Call the listener. ... if process success, (the flow executed correctly)
        // Then ... otherwise wait...
        return true;
    }
}
