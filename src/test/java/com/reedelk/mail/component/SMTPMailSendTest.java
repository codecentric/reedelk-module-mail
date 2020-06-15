package com.reedelk.mail.component;

import com.icegreen.greenmail.util.ServerSetup;
import com.reedelk.mail.component.smtp.BodyDefinition;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.icegreen.greenmail.util.ServerSetup.PORT_SMTP;
import static com.icegreen.greenmail.util.ServerSetup.PROTOCOL_SMTP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;


class SMTPMailSendTest extends AbstractMailTest {

    private ServerSetup serverSetup = new ServerSetup(1000 + PORT_SMTP, null, PROTOCOL_SMTP);

    private SMTPMailSend component = new SMTPMailSend();

    @Mock
    private ConverterService converterService;

    @BeforeEach
    void setUp() {
        super.setUp();
        SMTPConfiguration configuration = new SMTPConfiguration();
        configuration.setPort(serverSetup.getPort());
        configuration.setHost(address);
        configuration.setUsername(username);
        configuration.setPassword(password);

        mockScriptEngineEvaluation();
        component.setConnection(configuration);
        component.scriptService = scriptEngine;
        component.converterService = converterService;
    }

    @Test
    void shouldUseMessagePayloadAsMessageBody() throws MessagingException, IOException {
        // Given
        String mailMessageBodyText = "Mail message body";
        byte[] payload = mailMessageBodyText.getBytes();
        doReturn(payload).when(message).payload();
        doReturn(mailMessageBodyText).when(converterService).convert(payload, String.class);
        BodyDefinition bodyDefinition = new BodyDefinition();

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
        assertThatBodyContentIs(received, "Mail message body\r\n");
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
        assertThat(attributes).containsEntry("component", "com.reedelk.mail.component.SMTPMailSend");
        assertThat(attributes).containsEntry("subject", "My email subject");

        assertThat(attributes).containsEntry("from","from@test.com");
        assertThat(attributes).containsEntry("replyTo", asSerializableList("replyTo@test.com"));
        assertThat(attributes).containsEntry("to", asSerializableList("to@test.com"));
        assertThat(attributes).containsEntry("cc", asSerializableList("cc@test.com"));
        assertThat(attributes).containsEntry("bcc", asSerializableList("bcc@test.com"));
    }

    @Override
    protected ServerSetup serverSetup() {
        return serverSetup;
    }

    private ArrayList<String> asSerializableList(String ...elements) {
        ArrayList<String> out = new ArrayList<>();
        Collections.addAll(out, elements);
        return out;
    }
}
