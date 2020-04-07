package com.reedelk.mail.internal.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Poller {

    private final ProtocolPollingStrategy pollingStrategy;
    private final ExecutorService threadPool;
    private final List<Runnable> pollingThreads = new ArrayList<>();

    public Poller(ProtocolPollingStrategy pollingStrategy) {
        this.pollingStrategy = pollingStrategy;
        this.threadPool = Executors.newFixedThreadPool(1);//TODO: Parameter?
    }

    public void start() {
        Runnable worker = new PollerThread(pollingStrategy);
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
        private boolean isAlive = true;

        PollerThread(ProtocolPollingStrategy pollingStrategy) {
            this.pollingStrategy = pollingStrategy;
        }

        @Override
        public void run() {
            while (this.isAlive) {
                try {
                    pollingStrategy.poll();
                    Thread.sleep(10000); // TODO: Polling delay
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
