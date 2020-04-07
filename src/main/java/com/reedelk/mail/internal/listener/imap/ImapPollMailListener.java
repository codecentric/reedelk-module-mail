package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.listener.MailListenerInterface;
import com.reedelk.mail.internal.listener.Poller;

public class ImapPollMailListener implements MailListenerInterface {

    private final Poller poller;

    public ImapPollMailListener(IMAPConfiguration imapConfiguration) {
        ImapPollingStrategy pollingStrategy = new ImapPollingStrategy(imapConfiguration);
        this.poller = new Poller(pollingStrategy);
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
