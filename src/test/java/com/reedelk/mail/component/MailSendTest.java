package com.reedelk.mail.component;

import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;


class MailSendTest extends AbstractMailTest {

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
    void shouldCorrectlySendEmailWithJustFromAndTo() throws MessagingException, IOException {
        // Given
        component.setTo(DynamicString.from("to@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
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
    }

    @Test
    void shouldCorrectlySendEmailWithFromToSubjectAndBody() throws MessagingException, IOException {
        // Given
        BodyDefinition bodyDefinition = new BodyDefinition();
        bodyDefinition.setContent(DynamicString.from("My email body"));

        component.setBody(bodyDefinition);
        component.setTo(DynamicString.from("to@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
        component.setSubject(DynamicString.from("My email subject"));
        component.initialize();

        // When
        component.apply(context, message);

        // Then
        assertReceivedMessagesCountIs(1);

        MimeMessage received = firstReceivedMessage();

        assertThatToIs(received, "to@test.com");
        assertThatFromIs(received, "from@test.com");
        assertThatSubjectIs(received, "My email subject");
        assertThatBodyContentIs(received, "My email body");
    }

    @Test
    void shouldCorrectlySendEmailWithFromToCcBccAndReplyTo() throws MessagingException {
        // Given
        component.setTo(DynamicString.from("to@test.com"));
        component.setCc(DynamicString.from("cc@test.com"));
        component.setBcc(DynamicString.from("bcc@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
        component.setReplyTo(DynamicString.from("replyTo@test.com"));
        component.initialize();

        // When
        component.apply(context, message);

        // Then
        assertReceivedMessagesCountIs(3); // to, cc, bcc

        // To
        MimeMessage first = receivedMessage(0); // to
        assertThatToIs(first, "to@test.com");
        assertThatFromIs(first, "from@test.com");
        assertThatCcIs(first, "cc@test.com");
        assertThatReplyToIs(first, "replyTo@test.com");

        // Cc
        MimeMessage second = receivedMessage(1); // cc
        assertThatToIs(second, "to@test.com");
        assertThatFromIs(second, "from@test.com");
        assertThatCcIs(second, "cc@test.com");
        assertThatReplyToIs(second, "replyTo@test.com");

        // Bcc
        MimeMessage third = receivedMessage(2); // bcc
        assertThatToIs(third, "to@test.com");
        assertThatFromIs(third, "from@test.com");
        assertThatCcIs(third, "cc@test.com");
        assertThatReplyToIs(third, "replyTo@test.com");
    }
}