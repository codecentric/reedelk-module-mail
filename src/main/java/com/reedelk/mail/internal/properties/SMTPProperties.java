package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.SMTPConfiguration;
import com.reedelk.mail.component.SMTPProtocol;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

public class SMTPProperties extends Properties {

    private static final int DEFAULT_SMTP_PORT = 25;
    private static final int DEFAULT_SMTPS_PORT = 465;

    public SMTPProperties(SMTPConfiguration configuration) {
        SMTPProtocol protocol = Optional.ofNullable(configuration.getProtocol()).orElse(SMTPProtocol.SMTP);

        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));

        Integer connectionTimeout = Optional.ofNullable(configuration.getConnectTimeout()).orElse(60000);
        Integer socketTimeout = Optional.ofNullable(configuration.getSocketTimeout()).orElse(30000);


        boolean startTlsEnable = Optional.ofNullable(configuration.getStartTlsEnabled()).orElse(false);
        setProperty("mail.smtp.starttls.enable", String.valueOf(startTlsEnable));
        setProperty("mail.smtp.host", host);
        setProperty("mail.smtp.auth", Boolean.TRUE.toString());
        setProperty("mail.smtp.timeout", String.valueOf(socketTimeout));
        setProperty("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));

        if (SMTPProtocol.SMTP.equals(protocol)) {
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_SMTP_PORT);
            setProperty("mail.transport.protocol", "smtp");
            setProperty("mail.smtp.host", host);
            setProperty("mail.smtp.port", String.valueOf(port));

        } else {
            // SMTPs
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_SMTPS_PORT);
            setProperty("mail.transport.protocol", "smtps");
            setProperty("mail.smtp.ssl.enable", "true");
            setProperty("mail.smtp.port", String.valueOf(port));
        }
    }
}
