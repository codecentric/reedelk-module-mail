package com.reedelk.mail.component;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static javax.mail.Flags.Flag.*;
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
    void shouldCorrectlyPollAndMessageContainsFromSubjectAndBody() throws MessagingException, InterruptedException {
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
    void shouldCorrectlyPollAndMarkMessageAsSeenAfterPoll() throws MessagingException, InterruptedException {
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

    @Test
    void shouldCorrectlyPollAndNotMarkMessageAsSeenAfterPollWhenPeek() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";

        listener.setPeek(true);

        // When
        deliverMessage(from, subject, body);
        assumeFalse(existMessageWithFlag(SEEN));

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        assertThat(existMessageWithFlag(SEEN)).isFalse();
    }

    @Test
    void shouldCorrectlyPollAndMarkDeleteMessageAfterPoll() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";

        listener.setMarkDeleteOnSuccess(true);

        // When
        deliverMessage(from, subject, body);
        assumeFalse(existMessageWithFlag(DELETED));

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        assertThat(existMessageWithFlag(DELETED)).isTrue();
    }

    @Test
    void shouldCorrectlyPollAndDeleteMessageAfterPoll() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";

        listener.setDeleteOnSuccess(true);

        // When
        deliverMessage(from, subject, body);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        assertReceivedMessagesIsEmpty();
    }

    @Test
    void shouldPollSeenMessagesOnly() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setSeen(IMAPFlag.YES);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(SEEN, false);
        deliverRandomMessage(SEEN, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(1);
    }

    @Test
    void shouldPollSeenAndUnseenMessages() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setSeen(IMAPFlag.BOTH);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(SEEN, false);
        deliverRandomMessage(SEEN, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(2);
    }

    @Test
    void shouldPollDeletedMessagesOnly() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setDeleted(IMAPFlag.YES);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(DELETED, false);
        deliverRandomMessage(DELETED, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(1);
    }

    @Test
    void shouldPollDeletedAndUnDeletedMessages() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setDeleted(IMAPFlag.BOTH);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(DELETED, false);
        deliverRandomMessage(DELETED, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(2);
    }

    @Test
    void shouldPollAnsweredMessagesOnly() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setAnswered(IMAPFlag.YES);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(ANSWERED, false);
        deliverRandomMessage(ANSWERED, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(1);
    }

    @Test
    void shouldPollAnsweredAndNotAnsweredMessages() throws MessagingException, InterruptedException {
        // Given
        IMAPFlags imapFlags = new IMAPFlags();
        imapFlags.setAnswered(IMAPFlag.BOTH);
        listener.setFlags(imapFlags);
        listener.setBatch(true);

        // When
        deliverRandomMessage(ANSWERED, false);
        deliverRandomMessage(ANSWERED, true);

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(2);
    }

    @Test
    void shouldLimitNumberOfMessagesCorrectly() throws MessagingException, InterruptedException {
        // Given
        listener.setLimit(3);
        listener.setBatch(true);

        // When
        deliverRandomMessage();
        deliverRandomMessage();
        deliverRandomMessage();
        deliverRandomMessage();
        deliverRandomMessage();

        // Then
        Optional<Message> maybeInputMessage = pollMessage();
        assertThat(maybeInputMessage).isPresent();

        Message message = maybeInputMessage.get();
        List<Map<String,Object>> emails = message.payload();
        assertThat(emails).hasSize(3);
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

    private void deliverRandomMessage(Flags.Flag flag, boolean flagValue) throws MessagingException {
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";
        deliverMessage(from, subject, body, flag, flagValue);
    }

    private void deliverRandomMessage() throws MessagingException {
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body";
        deliverMessage(from, subject, body);
    }

    static class Result {
        Message message;
    }
}