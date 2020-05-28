package com.reedelk.mail.component;

import com.icegreen.greenmail.util.ServerSetup;
import com.reedelk.mail.component.imap.IMAPListeningStrategy;
import com.reedelk.mail.component.imap.IMAPProtocol;
import com.reedelk.mail.internal.CloseableService;
import com.reedelk.mail.internal.type.MailMessage;
import com.reedelk.runtime.api.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.util.Optional;

import static com.icegreen.greenmail.util.ServerSetup.PORT_IMAPS;
import static com.icegreen.greenmail.util.ServerSetup.PROTOCOL_IMAPS;
import static org.assertj.core.api.Assertions.assertThat;

public class IMAPsMailListenerPollingTest extends AbstractMailTest {

    private ServerSetup serverSetup = new ServerSetup(1000 + PORT_IMAPS, null, PROTOCOL_IMAPS);

    private CloseableService closeableService = new CloseableService();

    private IMAPMailListener listener;

    @BeforeEach
    void setUp() {
        super.setUp();
        IMAPConfiguration configuration = new IMAPConfiguration();
        configuration.setProtocol(IMAPProtocol.IMAPS);
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHost(address);
        configuration.setPort(serverSetup.getPort());

        // the test server would not be trusted, because the certificate
        // would not be in the Java distribution. Therefore we need to set this condition.
        configuration.setTrustedHosts("*");

        listener = new IMAPMailListener();
        listener.closeableService = closeableService;
        listener.setStrategy(IMAPListeningStrategy.POLLING);
        listener.setConnection(configuration);
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

        MailMessage payload = inputMessage.payload();
        assertThat(payload.get("body")).isEqualTo(body);
        assertThat(payload.get("subject")).isEqualTo(subject);
        assertThat(payload.get("from")).isEqualTo(from);
    }

    @Override
    protected ServerSetup serverSetup() {
        return serverSetup;
    }
}
