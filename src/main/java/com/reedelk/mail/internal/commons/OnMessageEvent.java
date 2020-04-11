package com.reedelk.mail.internal.commons;

import com.reedelk.mail.internal.FireEventOnResult;
import com.reedelk.runtime.api.component.Component;
import com.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OnMessageEvent {

    private static final Logger logger = LoggerFactory.getLogger(OnMessageEvent.class);

    public static boolean fire(Class<? extends Component> componentClazz, InboundEventListener listener, Message mail) {
        try {
            com.reedelk.runtime.api.message.Message message = MailMessageToMessageMapper.map(componentClazz, mail);
            return fireEventAndWaitResult(listener, message);
        } catch (Exception exception) {
            String error = String.format("Could not process mail message=[%s]", exception.getMessage());
            logger.error(error);
            return false;
        }
    }

    public static boolean fire(Class<? extends Component> componentClazz,
                               InboundEventListener listener,
                               Message[] mails) {
        try {
            com.reedelk.runtime.api.message.Message inMessage = MailMessageToMessageMapper.map(componentClazz, mails);
            return fireEventAndWaitResult(listener, inMessage);
        } catch (Exception exception) {
            String error = String.format("Could not process Mail Message IMAP Message=[%s]", exception.getMessage());
            logger.error(error);
            return false;
        }
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
