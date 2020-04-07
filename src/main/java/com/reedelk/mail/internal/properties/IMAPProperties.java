package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

public class IMAPProperties extends Properties {

    private static final int DEFAULT_IMAP_PORT = 143;

    public IMAPProperties(IMAPConfiguration configuration) {
        /**
         *  String host = imapConfiguration.getHost(); // or throw
         *         Integer port = imapConfiguration.getPort(); // or default
         *         Integer timeout = imapConfiguration.getTimeout(); // or default
         */
        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));
        Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_IMAP_PORT);

        // Switch to secure or not
        setProperty("mail.transport.protocol", "imaps");
        setProperty("mail.store.protocol", "imaps");
        setProperty("mail.imaps.port", String.valueOf(port));
        setProperty("mail.imaps.host", host);
    }

}
