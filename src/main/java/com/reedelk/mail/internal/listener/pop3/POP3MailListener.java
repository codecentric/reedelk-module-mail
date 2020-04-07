package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.listener.MailListenerInterface;
import com.reedelk.mail.internal.listener.Poller;

public class POP3MailListener implements MailListenerInterface {

    private Poller poller;

    public POP3MailListener(POP3Configuration configuration) {
        POP3PollingStrategy pop3PollingStrategy = new POP3PollingStrategy();
        poller = new Poller(pop3PollingStrategy);
    }

    @Override
    public void start() {
        poller.start();
    }

    @Override
    public void stop() {
        poller.stop();
    }
}
