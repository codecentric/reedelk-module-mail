package com.reedelk.mail.internal;

import com.reedelk.mail.internal.commons.Defaults;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class MailPoller implements Closeable {

    private static final int TERMINATION_AWAIT_TIME = 60000;

    private ScheduledExecutorService executorService;
    private PollingStrategy pollingStrategy;
    private ScheduledFuture<?> scheduled;

    public MailPoller() {
         executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void schedule(Integer pollInterval, PollingStrategy pollingStrategy) {
        this.pollingStrategy = pollingStrategy;
        int realPollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        this.scheduled = executorService.scheduleWithFixedDelay(pollingStrategy, 0L, realPollInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        if (pollingStrategy != null) {
            // First we stop the poller in order to stop polling the mail server.
            pollingStrategy.stop();
        }

        if (scheduled != null) {
            // We do not interrupt because we want the current message
            // being fetched to be processed correctly.
            scheduled.cancel(false);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TERMINATION_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
        }
    }
}
