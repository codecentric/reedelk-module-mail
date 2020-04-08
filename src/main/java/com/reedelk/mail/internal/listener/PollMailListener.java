package com.reedelk.mail.internal.listener;

import com.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;

public class PollMailListener implements ProtocolMailListener {

    private final Poller poller;

    public PollMailListener(ProtocolPollingStrategy pollingStrategy, Integer pollInterval) {
        int realPollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        this.poller = new Poller(pollingStrategy, realPollInterval);
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
