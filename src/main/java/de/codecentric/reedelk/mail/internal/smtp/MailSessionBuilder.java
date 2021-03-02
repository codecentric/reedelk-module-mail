package de.codecentric.reedelk.mail.internal.smtp;

import de.codecentric.reedelk.mail.component.SMTPConfiguration;
import de.codecentric.reedelk.mail.component.smtp.SMTPProtocol;
import de.codecentric.reedelk.mail.internal.commons.Defaults;
import de.codecentric.reedelk.mail.internal.exception.MailMessageConfigurationException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;

import java.util.Optional;

public class MailSessionBuilder {

    private final Email email;
    private SMTPConfiguration configuration;

    public static MailSessionBuilder builder(Email email) {
        return new MailSessionBuilder(email);
    }

    private MailSessionBuilder(Email email) {
        this.email = email;
    }

    public MailSessionBuilder configuration(SMTPConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public void build() {
        SMTPProtocol protocol = Optional.ofNullable(configuration.getProtocol()).orElse(SMTPProtocol.SMTP);

        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));

        boolean startTlsEnable = Optional.ofNullable(configuration.getStartTlsEnabled()).orElse(Defaults.TLS_ENABLE);
        Integer connectionTimeout = Optional.ofNullable(configuration.getConnectTimeout()).orElse(Defaults.CONNECT_TIMEOUT);
        Integer socketTimeout = Optional.ofNullable(configuration.getSocketTimeout()).orElse(Defaults.SOCKET_TIMEOUT);

        email.setHostName(host);
        email.setSmtpPort(configuration.getPort());
        email.setSocketTimeout(socketTimeout);
        email.setSocketConnectionTimeout(connectionTimeout);
        email.setAuthenticator(new DefaultAuthenticator(configuration.getUsername(), configuration.getPassword()));
        if (SMTPProtocol.SMTPS.equals(protocol)) {
            email.setSSLOnConnect(true);
            email.setSslSmtpPort(Integer.toString(configuration.getPort()));
        }
        if (startTlsEnable) {
            email.setStartTLSEnabled(configuration.getStartTlsEnabled());
        }
    }
}
