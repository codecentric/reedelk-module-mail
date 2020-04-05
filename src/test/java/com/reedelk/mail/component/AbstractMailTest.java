package com.reedelk.mail.component;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
abstract class AbstractMailTest {

    static final int smtpPort = 2525;
    static final String smtpAddress = "localhost";
    static final String smtpUsername = "testUser";
    static final String smtpPassword = "testPassword";

    private GreenMail mailServer;

    @Mock
    protected Message message;
    @Mock
    protected FlowContext context;
    @Mock
    protected ScriptEngineService scriptEngine;

    @BeforeEach
    void setUp() {
        ServerSetup serverSetup = new ServerSetup(smtpPort, smtpAddress, "smtp");
        mailServer = new GreenMail(serverSetup);
        mailServer.setUser(smtpUsername, smtpPassword);
        mailServer.start();
    }

    @AfterEach
    void tearDown() {
        if (mailServer != null) {
            mailServer.stop();
        }
    }

    protected MimeMessage firstReceivedMessage() {
        return mailServer.getReceivedMessages()[0];
    }

    protected MimeMessage receivedMessage(int index) {
        return mailServer.getReceivedMessages()[index];
    }

    protected void assertReceivedMessagesCountIs(int expected) {
        assertThat(mailServer.getReceivedMessages()).hasSize(expected);
    }

    protected void assertThatToIs(MimeMessage received, String ...expected) throws MessagingException {
        String[] tos = received.getHeader("to");
        assertThat(tos).containsExactly(expected);
    }

    protected void assertThatCcIs(MimeMessage received, String ...expected) throws MessagingException {
        String[] ccs = received.getHeader("cc");
        assertThat(ccs).containsExactly(expected);
    }

    protected void assertThatBccIs(MimeMessage received, String ...expected) throws MessagingException {
        String[] bccs = received.getHeader("bcc");
        assertThat(bccs).containsExactly(expected);
    }

    protected void assertThatReplyToIs(MimeMessage received, String ...expected) throws MessagingException {
        String[] replyTos = received.getHeader("Reply-To");
        assertThat(replyTos).containsExactly(expected);
    }

    protected void assertThatFromIs(MimeMessage received, String expected) throws MessagingException {
        Address[] from = received.getFrom();
        assertThat(from[0].toString()).isEqualTo(expected);
    }

    protected void assertThatSubjectIs(MimeMessage received, String expected) throws MessagingException {
        assertThat(received.getSubject()).isEqualTo(expected);
    }

    protected void assertThatBodyContentIs(MimeMessage received, String expected) throws IOException, MessagingException {
        MimeMultipart content = (MimeMultipart) received.getContent();
        BodyPart bodyPart = content.getBodyPart(0);
        assertThat(bodyPart.getContent()).isEqualTo(expected);
    }

    protected void mockScriptEngineEvaluation() {
        lenient().doAnswer(invocation -> {
            DynamicValue<?> dynamicValue = invocation.getArgument(0);
            if (dynamicValue == null) return Optional.empty();
            return Optional.ofNullable(dynamicValue.value());
        }).when(scriptEngine).evaluate(any(DynamicValue.class), eq(context), eq(message));
    }
}
