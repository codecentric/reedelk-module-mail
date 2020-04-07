package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

public class POP3Properties extends Properties {

    private static final int DEFAULT_POP3_PORT = 110;

    public POP3Properties(POP3Configuration configuration) {
        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));
        Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_POP3_PORT);

        setProperty("mail.transport.protocol", "pop3s");
        setProperty("mail.store.protocol", "pop3s");
        setProperty("mail.pop3s.host", host);
        setProperty("mail.pop3s.port", String.valueOf(port));
    }
}
