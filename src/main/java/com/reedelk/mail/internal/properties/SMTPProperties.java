package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.SMTPConfiguration;
import com.reedelk.mail.component.SMTPProtocol;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

import static com.reedelk.mail.internal.commons.Defaults.SMTP;
import static com.reedelk.mail.internal.commons.Defaults.SMTPs;

public class SMTPProperties extends Properties {

    public SMTPProperties(SMTPConfiguration configuration) {
        SMTPProtocol protocol = Optional.ofNullable(configuration.getProtocol()).orElse(SMTPProtocol.SMTP);

        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));

        boolean startTlsEnable = Optional.ofNullable(configuration.getStartTlsEnabled()).orElse(Defaults.TLS_ENABLE);
        Integer connectionTimeout = Optional.ofNullable(configuration.getConnectTimeout()).orElse(Defaults.CONNECT_TIMEOUT);
        Integer socketTimeout = Optional.ofNullable(configuration.getSocketTimeout()).orElse(Defaults.SOCKET_TIMEOUT);

        setProperty("mail.smtp.host", host);
        setProperty("mail.smtp.auth", Boolean.TRUE.toString());
        setProperty("mail.smtp.timeout", String.valueOf(socketTimeout));
        setProperty("mail.smtp.starttls.enable", String.valueOf(startTlsEnable));
        setProperty("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));

        if (SMTPProtocol.SMTP.equals(protocol)) {
            // SMTP
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(SMTP.DEFAULT_PORT);
            setProperty("mail.transport.protocol", SMTP.TRANSPORT);
            setProperty("mail.smtp.port", String.valueOf(port));

        } else {
            // SMTPs
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(SMTPs.DEFAULT_PORT);
            setProperty("mail.transport.protocol", SMTPs.TRANSPORT);
            setProperty("mail.smtp.port", String.valueOf(port));
        }
    }
}
