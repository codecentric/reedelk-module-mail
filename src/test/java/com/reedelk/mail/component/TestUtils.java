package com.reedelk.mail.component;

import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    // TODO: This class has  a lot of duplicated code!!

    // The result is the input message starting the flow
    public static Optional<Message> poll(IMAPMailListener listener, FlowContext context) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();

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

    // The result is the input message starting the flow
    public static Optional<Message> pollAndOnResultError(IMAPMailListener listener, FlowContext context) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();

        listener.addEventListener((message, onResult) -> {
            result.message = message;
            onResult.onError(context, new ESBException("my exception"));
            latch.countDown();
        });

        listener.onStart();
        latch.await(3, TimeUnit.SECONDS);
        listener.onShutdown();

        return Optional.ofNullable(result.message);
    }

    // The result is the input message starting the flow
    public static Optional<Message> poll(POP3MailListener listener, FlowContext context) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();

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

    // The result is the input message starting the flow
    public static Optional<Message> pollAndOnResultError(POP3MailListener listener, FlowContext context) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();

        listener.addEventListener((message, onResult) -> {
            result.message = message;
            onResult.onError(context, new ESBException("my exception"));
            latch.countDown();
        });

        listener.onStart();
        latch.await(3, TimeUnit.SECONDS);
        listener.onShutdown();

        return Optional.ofNullable(result.message);
    }

    private static class Result {
        Message message;
    }
}
