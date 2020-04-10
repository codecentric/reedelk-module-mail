package com.reedelk.mail.internal;

import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Message;
import java.util.concurrent.CountDownLatch;

public abstract class AbstractPollingStrategy implements PollingStrategy {

    protected final InboundEventListener listener;

    public AbstractPollingStrategy(InboundEventListener listener) {
        this.listener = listener;
    }

    protected boolean processMessage(Class<? extends Component> componentClazz, Message message) throws InterruptedException {
        com.reedelk.runtime.api.message.Message inMessage =
                MailMessageToMessageMapper.map(componentClazz, message);
        return fireEventAndWaitResult(inMessage);
    }

    protected boolean processMessages(Class<? extends Component> componentClazz, Message[] messages) throws Exception {
        com.reedelk.runtime.api.message.Message inMessage =
                MailMessageToMessageMapper.map(componentClazz, messages);
        return fireEventAndWaitResult(inMessage);
    }

    private boolean fireEventAndWaitResult(com.reedelk.runtime.api.message.Message inMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        FireEventOnResult fireEvent = new FireEventOnResult(latch);

        this.listener.onEvent(inMessage, fireEvent);

        latch.await(); // TODO: Should we add a timeout here?

        return fireEvent.result();
    }
}
