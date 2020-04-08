package com.reedelk.mail.internal.listener;

import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Message;

public abstract class AbstractPollingStrategy implements PollingStrategy {

    protected final InboundEventListener listener;

    public AbstractPollingStrategy(InboundEventListener listener) {
        this.listener = listener;
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
