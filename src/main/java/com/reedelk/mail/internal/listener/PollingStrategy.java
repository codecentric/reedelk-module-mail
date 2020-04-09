package com.reedelk.mail.internal.listener;

public interface PollingStrategy extends Runnable {

    void stop();
}
