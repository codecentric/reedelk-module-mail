package com.reedelk.mail.internal;

import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.PollingStrategy;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SchedulerProvider {

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduled;

    public SchedulerProvider() {
         executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void schedule(Integer pollInterval, PollingStrategy pollingStrategy) {
        int realPollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        this.scheduled = executorService.scheduleWithFixedDelay(pollingStrategy, realPollInterval, realPollInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (this.scheduled != null) scheduled.cancel(false);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
