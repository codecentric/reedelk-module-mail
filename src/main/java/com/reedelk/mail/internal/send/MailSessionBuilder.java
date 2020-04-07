package com.reedelk.mail.internal.send;

import com.reedelk.mail.component.SMTPConfiguration;
import com.reedelk.mail.internal.properties.SMTPProperties;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;

import javax.mail.Session;

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
        SMTPProperties smtpProperties = new SMTPProperties(configuration, true);

        String username = configuration.getUsername();
        String password = configuration.getPassword();

        Session session = Session.getInstance(smtpProperties, new DefaultAuthenticator(username, password));

        email.setMailSession(session);
    }
}
