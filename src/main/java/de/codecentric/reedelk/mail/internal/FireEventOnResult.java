package de.codecentric.reedelk.mail.internal;

import de.codecentric.reedelk.runtime.api.component.OnResult;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;

import java.util.concurrent.CountDownLatch;

public class FireEventOnResult implements OnResult {

    private final EventFiredResult event = new EventFiredResult();
    private final CountDownLatch latch;

    public FireEventOnResult(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onResult(FlowContext flowContext, de.codecentric.reedelk.runtime.api.message.Message message) {
        event.result = true;
        latch.countDown();
    }

    @Override
    public void onError(FlowContext flowContext, Throwable throwable) {
        event.result = false;
        latch.countDown();
    }

    public boolean result() {
        return event.result;
    }

    private static class EventFiredResult {
        boolean result;
    }
}
