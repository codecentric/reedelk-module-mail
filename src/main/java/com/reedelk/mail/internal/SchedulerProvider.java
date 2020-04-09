package com.reedelk.mail.internal;

import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.PollingStrategy;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.osgi.service.component.annotations.ServiceScope.SINGLETON;

@Component(service = SchedulerProvider.class, scope = SINGLETON)
public class SchedulerProvider {

    private ScheduledExecutorService executorService;
    private List<ScheduledFuture<?>> futures = new ArrayList<>();


    public synchronized ScheduledFuture<?> schedule(Integer pollInterval, PollingStrategy pollingStrategy) {
        if (executorService == null) {
            // Lazy loading
            this.executorService = Executors.newScheduledThreadPool(1);
        }

        int realPollInterval = Optional.ofNullable(pollInterval).orElse(Defaults.Poller.DEFAULT_POLL_INTERVAL);
        ScheduledFuture<?> scheduledFuture =
                executorService.scheduleWithFixedDelay(pollingStrategy, realPollInterval, realPollInterval, TimeUnit.MILLISECONDS);
        futures.add(scheduledFuture);
        return scheduledFuture;
    }

    public synchronized void cancel(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(false);
            futures.remove(future);
        }
    }

    public void stop() {
        futures.forEach(future -> future.cancel(false));
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Await termination.
        }
    }
}
