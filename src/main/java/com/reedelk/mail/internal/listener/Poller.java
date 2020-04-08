package com.reedelk.mail.internal.listener;

import com.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Poller {

    private final PollingStrategy pollingStrategy;
    private final Integer pollInterval;
    private final ScheduledExecutorService executorService;


    public Poller(PollingStrategy pollingStrategy, Integer pollInterval) {
        this.pollingStrategy = pollingStrategy;
        this.pollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        executorService = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        executorService.scheduleWithFixedDelay(pollingStrategy, 0, pollInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO: Log me
            e.printStackTrace();
        }
    }
}
