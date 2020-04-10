package com.reedelk.mail.component;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static javax.mail.Flags.Flag.SEEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeFalse;

class IMAPMailListenerPollingTest extends AbstractMailTest {

    private static final String PROTOCOL = "imap";
    private static final int PORT = 1143;

    private IMAPMailListener listener;

    @BeforeEach
    void setUp() {
        super.setUp();
        IMAPConfiguration configuration = new IMAPConfiguration();
        configuration.setProtocol(IMAPProtocol.IMAP);
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHost(address);
        configuration.setPort(PORT);

        listener = new IMAPMailListener();
        listener.setStrategy(IMAPListeningStrategy.POLLING);
        listener.setConfiguration(configuration);
        listener.setPollInterval(1000);
    }

    @Test
    void shouldCorrectlyPollMessageWithFromSubjectAndBody() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";

        // When
        deliverMessage(from, subject, body);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message inputMessage = maybeInputMessage.get();

        String payload = inputMessage.payload();
        assertThat(payload).isEqualTo(body);

        MessageAttributes attributes = inputMessage.getAttributes();
        assertThat(attributes).containsEntry("subject", subject);
        assertThat(attributes).containsEntry("from", from);
    }

    @Test
    void shouldCorrectlyPollMarkMessageAsSeenAfterPoll() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";

        // When
        deliverMessage(from, subject, body);
        assumeFalse(existMessageWithFlag(SEEN));

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        assertThat(existMessageWithFlag(SEEN)).isTrue();
    }

    @Override
    protected String protocol() {
        return PROTOCOL;
    }

    @Override
    protected int port() {
        return PORT;
    }

    // The result is the input message starting the flow
    private Optional<Message> pollMessage() throws InterruptedException {
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

    private boolean existMessageWithFlag(Flags.Flag flag) throws MessagingException {
        MimeMessage mimeMessage = receivedMessage(0);
        return mimeMessage.getFlags().contains(flag);
    }

    static class Result {
        Message message;
    }
}