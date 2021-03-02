package de.codecentric.reedelk.mail.component;

import de.codecentric.reedelk.runtime.api.component.Inbound;
import de.codecentric.reedelk.runtime.api.component.InboundEventListener;
import de.codecentric.reedelk.runtime.api.exception.PlatformException;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static Optional<Message> poll(IMAPMailListener listener, FlowContext context) throws InterruptedException {
        return poll(listener,
                (message, onResult) ->
                        onResult.onResult(context, message));
    }

    public static Optional<Message> pollAndOnResultError(IMAPMailListener listener, FlowContext context) throws InterruptedException {
        return poll(listener,
                (message, onResult) ->
                        onResult.onError(context, new PlatformException("my exception")));
    }

    public static Optional<Message> poll(POP3MailListener listener, FlowContext context) throws InterruptedException {
        return poll(listener,
                (message, onResult) ->
                        onResult.onResult(context, message));
    }

    public static Optional<Message> pollAndOnResultError(POP3MailListener listener, FlowContext context) throws InterruptedException {
        return poll(listener,
                (message, onResult) ->
                        onResult.onError(context, new PlatformException("my exception")));
    }

    public static Optional<Message> poll(Inbound listener, InboundEventListener inboundEventListener) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Result result = new Result();
        listener.addEventListener((message, onResult) -> {
            inboundEventListener.onEvent(message, onResult);
            result.message = message;
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
