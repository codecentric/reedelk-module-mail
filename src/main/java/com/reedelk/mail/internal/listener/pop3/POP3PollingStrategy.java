package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.listener.PollingStrategy;

public class POP3PollingStrategy implements PollingStrategy {

    private final POP3Configuration configuration;

    public POP3PollingStrategy(POP3Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void poll() {

    }

}
