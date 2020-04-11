package com.reedelk.mail.component;

import com.reedelk.mail.component.imap.IMAPListeningStrategy;
import com.reedelk.mail.component.imap.IMAPProtocol;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.content.Attachment;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.util.Map;
import java.util.Optional;

import static com.icegreen.greenmail.util.GreenMailUtil.createMultipartWithAttachment;
import static org.assertj.core.api.Assertions.assertThat;

public class IMAPMailListenerPollingAttachmentTest extends AbstractMailTest {

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
