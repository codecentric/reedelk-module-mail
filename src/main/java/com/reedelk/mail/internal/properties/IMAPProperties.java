package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPProtocol;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

public class IMAPProperties extends Properties {

    public IMAPProperties(IMAPConfiguration configuration) {
        IMAPProtocol protocol = Optional.ofNullable(configuration.getProtocol()).orElse(IMAPProtocol.IMAP);

        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));

        boolean startTlsEnable = Optional.ofNullable(configuration.getStartTlsEnabled()).orElse(Defaults.TLS_ENABLE);
        Integer connectionTimeout = Optional.ofNullable(configuration.getConnectTimeout()).orElse(Defaults.CONNECT_TIMEOUT);
        Integer socketTimeout = Optional.ofNullable(configuration.getSocketTimeout()).orElse(Defaults.SOCKET_TIMEOUT);


        if (IMAPProtocol.IMAP.equals(protocol)) {
            // IMAP
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(Defaults.IMAP.DEFAULT_PORT);
            setProperty("mail.transport.protocol", Defaults.IMAP.TRANSPORT);
            setProperty("mail.store.protocol", Defaults.IMAP.TRANSPORT);
            setProperty("mail.imap.host", host);
            setProperty("mail.imap.port", String.valueOf(port));
            setProperty("mail.imap.auth", Boolean.TRUE.toString());
            setProperty("mail.imap.timeout", String.valueOf(socketTimeout));
            setProperty("mail.imap.starttls.enable", String.valueOf(startTlsEnable));
            setProperty("mail.imap.connectiontimeout", String.valueOf(connectionTimeout));

        } else {
            // IMAPs
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(Defaults.IMAPs.DEFAULT_PORT);
            setProperty("mail.transport.protocol", Defaults.IMAPs.TRANSPORT);
            setProperty("mail.store.protocol", Defaults.IMAPs.TRANSPORT);
            setProperty("mail.imaps.host", host);
            setProperty("mail.imaps.port", String.valueOf(port));
            setProperty("mail.imaps.auth", Boolean.TRUE.toString());
            setProperty("mail.imaps.timeout", String.valueOf(socketTimeout));
            setProperty("mail.imaps.starttls.enable", String.valueOf(startTlsEnable));
            setProperty("mail.imaps.connectiontimeout", String.valueOf(connectionTimeout));
        }
    }

}
