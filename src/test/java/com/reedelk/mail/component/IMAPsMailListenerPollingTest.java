package com.reedelk.mail.component;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class IMAPsMailListenerPollingTest extends AbstractMailTest {

    private static final String PROTOCOL = "imaps";
    private static final int PORT = 9993;

    private IMAPMailListener listener;

    @BeforeEach
    void setUp() {
        super.setUp();
        IMAPConfiguration configuration = new IMAPConfiguration();
        configuration.setProtocol(IMAPProtocol.IMAPS);
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHost(address);
        configuration.setPort(PORT);

        // the test server would not be trusted, because the certificate
        // would not be in the Java distribution. Therefore we need to set this condition.
        configuration.setTrustedHosts("*");

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
        Optional<Message> maybeInputMessage = TestUtils.poll(listener, context);
        assertThat(maybeInputMessage).isPresent();

        Message inputMessage = maybeInputMessage.get();

        String payload = inputMessage.payload();
        assertThat(payload).isEqualTo(body);

        MessageAttributes attributes = inputMessage.getAttributes();
        assertThat(attributes).containsEntry("subject", subject);
        assertThat(attributes).containsEntry("from", from);
    }

    @Override
    protected String protocol() {
        return PROTOCOL;
    }

    @Override
    protected int port() {
        return PORT;
    }
}
