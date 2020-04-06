package com.reedelk.mail.component;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicByteArray;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class MailSendAttachmentTest extends AbstractMailTest {

    private MailSend component = new MailSend();

    @BeforeEach
    void setUp() {
        super.setUp();
        SMTPConfiguration configuration = new SMTPConfiguration();
        configuration.setPort(smtpPort);
        configuration.setHost(smtpAddress);
        configuration.setUsername(smtpUsername);
        configuration.setPassword(smtpPassword);

        mockScriptEngineEvaluation();
        component.setConnectionConfiguration(configuration);
        component.scriptService = scriptEngine;
    }

    @Test
    void shouldCorrectlySendEmailWithAttachmentDefinition() throws MessagingException, IOException {
        // Given
        AttachmentDefinition attachment = new AttachmentDefinition();
        attachment.setSourceType(AttachmentSourceType.EXPRESSION);
        attachment.setName("My Attachment Name");
        attachment.setContentType(MimeType.AsString.TEXT_HTML);
        attachment.setExpression(DynamicByteArray.from("<h1>My attachment HTML</h1>"));
        attachment.setFileName(DynamicString.from("my-attachment.txt"));

        component.setTo(DynamicString.from("to@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
        component.setAttachments(Collections.singletonList(attachment));
        component.initialize();

        // When
        component.apply(context, message);

        // Then
        assertReceivedMessagesCountIs(1);

        MimeMessage received = firstReceivedMessage();

        assertThatToIs(received, "to@test.com");
        assertThatFromIs(received, "from@test.com");
        assertThatSubjectIs(received, null);
        assertThatBodyContentIs(received, StringUtils.EMPTY);

        MimeMultipart content = (MimeMultipart) received.getContent();
        BodyPart attachmentPart = content.getBodyPart(1);
        String attachment1Content = (String) attachmentPart.getContent();// Attachment 1 content
        assertThat(attachment1Content).isEqualTo("<h1>My attachment HTML</h1>");

        String[] contentType = attachmentPart.getHeader("Content-Type");
        assertThat(contentType).containsExactly("text/html; charset=UTF-8; name=my-attachment.txt");

        String[] transferEncoding = attachmentPart.getHeader("Content-Transfer-Encoding");
        assertThat(transferEncoding).containsExactly("7bit");
    }
}
