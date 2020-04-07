package com.reedelk.mail.internal.properties;

import com.reedelk.mail.component.SMTPConfiguration;
import com.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

public class SMTPProperties extends Properties {

    private static final int DEFAULT_SMTP_PORT = 587;

    public SMTPProperties(SMTPConfiguration configuration, boolean authenticate) {
        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));
        Integer port = Optional.ofNullable(configuration.getPort()).orElse(DEFAULT_SMTP_PORT);

        setProperty("mail.transport.protocol", "smtp");
        setProperty("mail.smtp.port", String.valueOf(port));
        setProperty("mail.smtp.host", host);
        setProperty("mail.debug", String.valueOf(false));
        setProperty("mail.smtp.auth", String.valueOf(authenticate));
       // setProperty("mail.smtp.starttls.enable", this.isStartTLSEnabled() ? "true" : "false");
        //setProperty("mail.smtp.starttls.required", this.isStartTLSRequired() ? "true" : "false");
        //setProperty("mail.smtp.sendpartial", this.isSendPartial() ? "true" : "false");
        //setProperty("mail.smtps.sendpartial", this.isSendPartial() ? "true" : "false");
        //if (this.authenticator != null) {
          //
        //}

        /**
        if (this.isSSLOnConnect()) {
            setProperty("mail.smtp.port", this.sslSmtpPort);
            setProperty("mail.smtp.socketFactory.port", this.sslSmtpPort);
            setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            setProperty("mail.smtp.socketFactory.fallback", "false");
        }

        if ((this.isSSLOnConnect() || this.isStartTLSEnabled()) && this.isSSLCheckServerIdentity()) {
            setProperty("mail.smtp.ssl.checkserveridentity", "true");
        }

        if (this.bounceAddress != null) {
            setProperty("mail.smtp.from", this.bounceAddress);
        }

        if (this.socketTimeout > 0) {
            setProperty("mail.smtp.timeout", Integer.toString(this.socketTimeout));
        }

        if (this.socketConnectionTimeout > 0) {
            setProperty("mail.smtp.connectiontimeout", Integer.toString(this.socketConnectionTimeout));
        }*/
    }
}
