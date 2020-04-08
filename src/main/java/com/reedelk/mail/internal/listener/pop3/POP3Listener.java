package com.reedelk.mail.internal.listener.pop3;

import com.reedelk.mail.component.POP3Configuration;
import com.reedelk.mail.internal.listener.Poller;
import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.runtime.api.component.InboundEventListener;

public class POP3Listener implements ProtocolMailListener {

    private Poller poller;

    public POP3Listener(POP3Configuration configuration, Integer pollInterval, Boolean deleteAfterRetrieve, InboundEventListener eventListener) {
        POP3PollingStrategy pop3PollingStrategy = new POP3PollingStrategy(configuration, deleteAfterRetrieve, eventListener);
        poller = new Poller(pop3PollingStrategy, pollInterval);
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
