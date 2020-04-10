package com.reedelk.mail.component;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    // The result is the input message starting the flow
    public static Optional<Message> poll(IMAPMailListener listener, FlowContext context) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        IMAPMailListenerPollingTest.Result result = new IMAPMailListenerPollingTest.Result();

        listener.addEventListener((message, onResult) -> {
            result.message = message;
            onResult.onResult(context, message);
            latch.countDown();
        });

        listener.onStart();
        latch.await(3, TimeUnit.SECONDS);
        listener.onShutdown();

        return Optional.ofNullable(result.message);
    }
}
