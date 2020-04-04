package com.reedelk.mail.component;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSendTest {

    @Mock
    private FlowContext context;
    @Mock
    private Message message;
    @Mock
    private ScriptEngineService mockScriptEngineService;

    private static GreenMail mailServer;
    private MailSend component = new MailSend();

    @BeforeAll
    static void setUp() {
        ServerSetup serverSetup = new ServerSetup(2525, "localhost", "smtp");
        mailServer = new GreenMail(serverSetup);
        mailServer.setUser("testUser", "testPassword");
        mailServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (mailServer != null) {
            mailServer.stop();
        }
        mailServer = null;
    }

    @Test
    void shouldCorrectlySendEmail() throws MessagingException, IOException {
        // Given
        SMTPConfiguration configuration = new SMTPConfiguration();
        configuration.setHost("localhost");
        configuration.setPort(2525);
        configuration.setUsername("testUser");
        configuration.setPassword("testPassword");

        component.setConnectionConfiguration(configuration);
        component.setFrom(DynamicString.from("from@test.com"));
        component.setTo(DynamicString.from("to@test.com"));
        component.setSubject(DynamicString.from("My email subject"));

        BodyDefinition bodyDefinition = new BodyDefinition();
        bodyDefinition.setContent(DynamicString.from("My email body"));
        component.setBody(bodyDefinition);

        doAnswer(invocation -> {
            DynamicValue<?> dynamicValue = invocation.getArgument(0);
            if (dynamicValue == null) return Optional.empty();
            return Optional.ofNullable(dynamicValue.value());
        }).when(mockScriptEngineService).evaluate(any(DynamicValue.class), eq(context), eq(message));

        component.scriptService = mockScriptEngineService;
        component.initialize();

        // When
        Message result = component.apply(context, message); // TODO: Assert result

        // Then
        assertThat(mailServer.getReceivedMessages()).hasSize(1);
        MimeMessage received = mailServer.getReceivedMessages()[0];

        assertThat(received.getSubject()).isEqualTo("My email subject");

        MimeMultipart content = (MimeMultipart) received.getContent();

        BodyPart bodyPart = content.getBodyPart(0);
        assertThat(bodyPart.getContent()).isEqualTo("My email body");
    }

}