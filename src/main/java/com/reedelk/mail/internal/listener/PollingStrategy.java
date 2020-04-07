package com.reedelk.mail.internal.listener;

public interface PollingStrategy {

    void poll();

    void setListener(OnMessageListener listener);
}
