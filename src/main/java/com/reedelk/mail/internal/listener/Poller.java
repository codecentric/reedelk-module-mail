package com.reedelk.mail.internal.listener;

public interface Poller {

    void poll();

    void setListener(OnMessageListener listener);
}
