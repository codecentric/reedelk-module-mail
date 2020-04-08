package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.listener.Poller;
import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.runtime.api.component.InboundEventListener;

public class ImapPollMailListener implements ProtocolMailListener {

    private final Poller poller;

    public ImapPollMailListener(IMAPConfiguration imapConfiguration, InboundEventListener eventListener) {
        ImapPollingStrategy pollingStrategy = new ImapPollingStrategy(imapConfiguration, eventListener);
        this.poller = new Poller(pollingStrategy, null); // TODO: Fixme
    }

    @Override
    public void start() {
        this.poller.start();
    }

    @Override
    public void stop() {
        this.poller.stop();
    }
}
