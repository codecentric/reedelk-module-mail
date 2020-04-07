package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.listener.MailListenerInterface;
import com.reedelk.mail.internal.listener.Poller;

public class ImapMailListener implements MailListenerInterface {

    private final IMAPConfiguration imapConfiguration;

    private final Poller poller;

    public ImapMailListener(IMAPConfiguration imapConfiguration) {
        this.imapConfiguration = imapConfiguration;
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
