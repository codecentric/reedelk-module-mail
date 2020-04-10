package com.reedelk.mail.internal;

import com.reedelk.mail.internal.commons.Defaults;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SchedulerProvider {

    private static final int TERMINATION_AWAIT_TIME = 60000;

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduled;

    public SchedulerProvider() {
         executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void schedule(Integer pollInterval, PollingStrategy pollingStrategy) {
        int realPollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        this.scheduled = executorService.scheduleWithFixedDelay(pollingStrategy, 0L, realPollInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (this.scheduled != null) {
            scheduled.cancel(false);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TERMINATION_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
