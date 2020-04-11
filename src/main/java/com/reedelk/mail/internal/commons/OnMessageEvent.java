package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.FireEventOnResult;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.Message;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OnMessageEvent {

    public static boolean fire(Class<? extends Component> componentClazz,
                               InboundEventListener listener,
                               Message mail) throws Exception {
        com.reedelk.runtime.api.message.Message inMessage = MailMessageToMessageMapper.map(componentClazz, mail);
        return fireEventAndWaitResult(listener, inMessage);
    }

    public static boolean fire(Class<? extends Component> componentClazz,
                           InboundEventListener listener,
                           Message[] mails) throws Exception {
        com.reedelk.runtime.api.message.Message inMessage = MailMessageToMessageMapper.map(componentClazz, mails);
        return fireEventAndWaitResult(listener, inMessage);
    }

    private static boolean fireEventAndWaitResult(InboundEventListener listener,
                                                  com.reedelk.runtime.api.message.Message inMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        FireEventOnResult fireEvent = new FireEventOnResult(latch);

        listener.onEvent(inMessage, fireEvent);

        // We give at most X seconds for the flow to complete the processing of the
        // mail message. After X seconds an error will be thrown.
        latch.await(Defaults.FLOW_MAX_MESSAGE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return fireEvent.result();
    }
}
