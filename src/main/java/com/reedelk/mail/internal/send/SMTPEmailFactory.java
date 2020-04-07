package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.SMTPConfiguration;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;

import java.util.Optional;

public class SMTPEmailFactory {

    private static final int DEFAULT_SMTP_PORT = 587;

    private final Email email;
    private SMTPConfiguration configuration;

    public static SMTPEmailFactory builder(Email email) {
        return new SMTPEmailFactory(email);
    }

    private SMTPEmailFactory(Email email) {
        this.email = email;
    }

    public SMTPEmailFactory configuration(SMTPConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public void build() {
        String username = configuration.getUsername();
        String password = configuration.getPassword();
        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));
        Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_SMTP_PORT);

        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        // TODO: Add SSL
    }
}
