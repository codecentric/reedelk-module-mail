package de.codecentric.reedelk.mail.component;

import com.icegreen.greenmail.util.ServerSetup;
import de.codecentric.reedelk.mail.component.pop3.POP3Protocol;
import de.codecentric.reedelk.mail.internal.CloseableService;
import de.codecentric.reedelk.mail.internal.type.MailMessage;
import de.codecentric.reedelk.runtime.api.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static com.icegreen.greenmail.util.ServerSetup.PORT_POP3S;
import static com.icegreen.greenmail.util.ServerSetup.PROTOCOL_POP3S;
import static org.assertj.core.api.Assertions.assertThat;

public class POP3sMailListenerPollingTest extends AbstractMailTest {

    private ServerSetup serverSetup = new ServerSetup(1000 + PORT_POP3S, null, PROTOCOL_POP3S);

    private CloseableService closeableService = new CloseableService();

    private POP3MailListener listener;

    @BeforeEach
    void setUp() {
        super.setUp();
        POP3Configuration configuration = new POP3Configuration();
        configuration.setProtocol(POP3Protocol.POP3S);
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHost(address);
        configuration.setPort(serverSetup.getPort());

        // the test server would not be trusted, because the certificate
        // would not be in the Java distribution. Therefore we need to set this condition.
        configuration.setTrustedHosts("*");

        listener = new POP3MailListener();
        listener.closeableService = closeableService;
        listener.setConnection(configuration);
        listener.setPollInterval(1000);
    }

    @Test
    void shouldCorrectlyPollAndMessageContainsFromSubjectAndBody() throws MessagingException, InterruptedException {
        // Given
        String from = "my-test@mydomain.com";
        String subject = "My sample subject";
        String body = "My sample body\r\n";

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

        MimeMessage mimeMessage = firstReceivedMessage();
        assertThat(mimeMessage).isNotNull();
    }

    @Override
    protected ServerSetup serverSetup() {
        return serverSetup;
    }
}
