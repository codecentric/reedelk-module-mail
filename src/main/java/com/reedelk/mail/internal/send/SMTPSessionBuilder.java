package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.SMTPConfiguration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class SMTPSessionBuilder {

    private SMTPConfiguration configuration;

    public static SMTPSessionBuilder builder() {
        return new SMTPSessionBuilder();
    }

    private SMTPSessionBuilder() {
    }

    public SMTPSessionBuilder configuration(SMTPConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public Session build() {
        final String username = configuration.getUsername();
        final String password = configuration.getPassword();
        String host = configuration.getHost();
        Integer port = configuration.getPort();

        Properties props = new Properties();
        props.put("mail.smtp.host", host); //SMTP Host
        props.put("mail.smtp.port", port); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        return Session.getInstance(props, auth);
    }
}
