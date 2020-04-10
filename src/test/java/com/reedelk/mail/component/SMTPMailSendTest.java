package com.reedelk.mail.component;

import com.reedelk.mail.component.smtp.BodyDefinition;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SMTPMailSendTest extends AbstractMailTest {

    private static final int SMTP_PORT = 2525;
    private static final String PROTOCOL = "smtp";

    private SMTPMailSend component = new SMTPMailSend();

    @BeforeEach
    void setUp() {
        super.setUp();
        SMTPConfiguration configuration = new SMTPConfiguration();
        configuration.setPort(SMTP_PORT);
        configuration.setHost(address);
        configuration.setUsername(username);
        configuration.setPassword(password);

        mockScriptEngineEvaluation();
        component.setConnectionConfiguration(configuration);
        component.scriptService = scriptEngine;
    }

    @Test
    void shouldThrowExceptionWhenBodyNotPresent() {
        // Given
        BodyDefinition bodyDefinition = new BodyDefinition();

        component.setBody(bodyDefinition);
        component.setTo(DynamicString.from("to@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
        component.initialize();

        // When
        MailMessageConfigurationException thrown = assertThrows(MailMessageConfigurationException.class,
                () -> component.apply(context, message));

        // Then
        assertThat(thrown).hasMessage("The mail body must not be empty");
        assertReceivedMessagesCountIs(0);
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
        assertThatBodyContentIs(received, "My email body\r\n");
    }

    @Test
    void shouldCorrectlySendEmailWithFromToCcBccAndReplyTo() throws MessagingException {
        // Given
        BodyDefinition bodyDefinition = new BodyDefinition();
        bodyDefinition.setContent(DynamicString.from("My email body"));

        component.setBody(bodyDefinition);
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

    @Test
    void shouldCorrectlySetOutMessageAttributes() {
        // Given
        BodyDefinition bodyDefinition = new BodyDefinition();
        bodyDefinition.setContent(DynamicString.from("My email body"));

        component.setBody(bodyDefinition);
        component.setTo(DynamicString.from("to@test.com"));
        component.setCc(DynamicString.from("cc@test.com"));
        component.setBcc(DynamicString.from("bcc@test.com"));
        component.setFrom(DynamicString.from("from@test.com"));
        component.setSubject(DynamicString.from("My email subject"));
        component.setReplyTo(DynamicString.from("replyTo@test.com"));
        component.initialize();

        // When
        Message actual = component.apply(context, message);

        // Then
        Object payload = actual.payload();
        assertThat(payload).isNull();

        MessageAttributes attributes = actual.getAttributes();
        assertThat(attributes).containsKeys("sentDate");
        assertThat(attributes).containsEntry("componentName", "SMTPMailSend");
        assertThat(attributes).containsEntry("subject", "My email subject");

        assertThat(attributes).containsEntry("from","from@test.com");
        assertThat(attributes).containsEntry("replyTo", asSerializableList("replyTo@test.com"));
        assertThat(attributes).containsEntry("to", asSerializableList("to@test.com"));
        assertThat(attributes).containsEntry("cc", asSerializableList("cc@test.com"));
        assertThat(attributes).containsEntry("bcc", asSerializableList("bcc@test.com"));
    }


    @Override
    protected String protocol() {
        return PROTOCOL;
    }

    @Override
    protected int port() {
        return SMTP_PORT;
    }

    private ArrayList<String> asSerializableList(String ...elements) {
        ArrayList<String> out = new ArrayList<>();
        Collections.addAll(out, elements);
        return out;
    }
}