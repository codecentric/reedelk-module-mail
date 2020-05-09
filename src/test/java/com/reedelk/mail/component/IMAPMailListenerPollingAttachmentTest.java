package com.reedelk.mail.component;

import com.icegreen.greenmail.util.ServerSetup;
import com.reedelk.mail.component.imap.IMAPListeningStrategy;
import com.reedelk.mail.component.imap.IMAPProtocol;
import com.reedelk.mail.internal.CloseableService;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.util.Map;
import java.util.Optional;

import static com.icegreen.greenmail.util.GreenMailUtil.createMultipartWithAttachment;
import static com.icegreen.greenmail.util.ServerSetup.PORT_IMAP;
import static com.icegreen.greenmail.util.ServerSetup.PROTOCOL_IMAP;
import static org.assertj.core.api.Assertions.assertThat;

public class IMAPMailListenerPollingAttachmentTest extends AbstractMailTest {

    private ServerSetup serverSetup = new ServerSetup(1000 + PORT_IMAP, null, PROTOCOL_IMAP);

    @Mock
    private CloseableService closeableService;

    private IMAPMailListener listener;

    @BeforeEach
    void setUp() {
        super.setUp();
        IMAPConfiguration configuration = new IMAPConfiguration();
        configuration.setProtocol(IMAPProtocol.IMAP);
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setHost(address);
        configuration.setPort(serverSetup.getPort());

        listener = new IMAPMailListener();
        listener.closeableService = closeableService;
        listener.setStrategy(IMAPListeningStrategy.POLLING);
        listener.setConnection(configuration);
        listener.setPollInterval(1000);
    }

    @Test
    void shouldPollMessageWithAttachment() throws MessagingException, InterruptedException {
        String body = "Test content body";
        String contentType = "text/plain";
        String attachmentFileName = "myfile.txt";
        byte[] attachment = "Attachment content".getBytes();
        String attachmentDescription = "This is a description";
        MimeMultipart multipartWithAttachment =
                createMultipartWithAttachment(body, attachment, contentType, attachmentFileName, attachmentDescription);
        deliverRandomMessageWithContent(multipartWithAttachment, "multipart/mixed");

        // When
        Optional<com.reedelk.runtime.api.message.Message> maybeInputMessage = TestUtils.poll(listener, context);

        // Then
        assertThat(maybeInputMessage).isPresent();

        Message inputMessage = maybeInputMessage.get();
        assertThat(inputMessage).isNotNull();

        TypedContent<String, String> content = inputMessage.getContent();
        assertThat(content.getMimeType()).isEqualTo(MimeType.TEXT_PLAIN);
        assertThat(content.data()).isEqualTo(body);

        MessageAttributes attributes = inputMessage.getAttributes();
        Map<String, Attachment> attachments = attributes.get("attachments");
        assertThat(attachments).containsOnlyKeys("myfile.txt");

        Attachment fileAttachment = attachments.get("myfile.txt");
        ByteArrayContent fileContent = fileAttachment.getContent();
        assertThat(fileContent.data()).isEqualTo(attachment);
        assertThat(fileAttachment.getName()).isEqualTo("myfile.txt");
    }

    @Override
    protected ServerSetup serverSetup() {
        return serverSetup;
    }
}
