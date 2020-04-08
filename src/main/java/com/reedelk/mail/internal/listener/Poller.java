package com.reedelk.mail.internal.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Poller {

    private static final int DEFAULT_POLL_INTERVAL = 60000;
    private final ProtocolPollingStrategy pollingStrategy;
    private final ExecutorService threadPool;
    private final List<Runnable> pollingThreads = new ArrayList<>();
    private final Integer pollInterval;

    public Poller(ProtocolPollingStrategy pollingStrategy, Integer pollInterval) {
        this.pollingStrategy = pollingStrategy;
        this.pollInterval = Optional.ofNullable(pollInterval).orElse(DEFAULT_POLL_INTERVAL);
        this.threadPool = Executors.newFixedThreadPool(1);//TODO: Parameter?
    }

    public void start() {
        Runnable worker = new PollerThread(pollingStrategy, pollInterval);
        this.pollingThreads.add(worker);
        this.threadPool.execute(worker);
    }

    public void stop() {
        if (!this.pollingThreads.isEmpty()) {
            for (Runnable pollingThread : this.pollingThreads) {
                ((PollerThread) pollingThread).stop();
            }
        }
        if (this.threadPool != null && !this.threadPool.isTerminated()) {
            this.threadPool.shutdown();
        }
    }

    private static class PollerThread implements Runnable {

        private final ProtocolPollingStrategy pollingStrategy;
        private final int pollInterval;
        private volatile boolean isAlive = true;

        PollerThread(ProtocolPollingStrategy pollingStrategy, int pollInterval) {
            this.pollingStrategy = pollingStrategy;
            this.pollInterval = pollInterval;
        }

        @Override
        public void run() {
            while (this.isAlive) {
                try {
                    if (!Thread.interrupted()) {
                        pollingStrategy.poll();
                        Thread.sleep(pollInterval);
                    }
                } catch (Exception ex) {
                    // suppress
                    // TODO: Check if we can interrupt immediately
                }
            }
        }

        public void stop() {
            this.isAlive = false;
        }
    }
}
