package com.reedelk.mail.internal.listener;

import javax.mail.Message;

public interface OnMessageListener {

    void onMessageReceived(Message newMessage);
}
