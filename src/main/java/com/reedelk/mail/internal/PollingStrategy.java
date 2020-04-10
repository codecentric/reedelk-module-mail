package com.reedelk.mail.internal;

public interface PollingStrategy extends Runnable {

    void stop();
}
