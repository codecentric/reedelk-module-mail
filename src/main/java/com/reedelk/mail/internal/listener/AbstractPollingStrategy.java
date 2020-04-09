package com.reedelk.mail.internal.listener;

import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.component.InboundEventListener;
import com.reedelk.runtime.api.component.OnResult;
import com.reedelk.runtime.api.flow.FlowContext;

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

    protected boolean processMessages(Class<? extends Component> componentClazz, Message[] messages) throws InterruptedException {
        com.reedelk.runtime.api.message.Message inMessage =
                MailMessageToMessageMapper.map(componentClazz, messages);
        return fireEventAndWaitResult(inMessage);
    }

    private boolean fireEventAndWaitResult(com.reedelk.runtime.api.message.Message inMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        FireEvent fireEvent = new FireEvent(latch);

        this.listener.onEvent(inMessage, fireEvent);

        latch.await(); // TODO: Should we add a timeout here?

        return fireEvent.result();
    }

    static class EventFiredResult {
        boolean result;
    }

    static class FireEvent implements OnResult {

        private final EventFiredResult event = new EventFiredResult();
        private final CountDownLatch latch;

        public FireEvent(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onResult(FlowContext flowContext, com.reedelk.runtime.api.message.Message message) {
            event.result = true;
            latch.countDown();
        }

        @Override
        public void onError(FlowContext flowContext, Throwable throwable) {
            event.result = false;
            latch.countDown();
        }

        boolean result() {
            return event.result;
        }
    }
}
