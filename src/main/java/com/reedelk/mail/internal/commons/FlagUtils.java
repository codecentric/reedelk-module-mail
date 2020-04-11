package com.reedelk.mail.internal.commons;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

public class FlagUtils {

    public static void set() {

    }

    public static void notSeen(Message message) {
        try {
            message.setFlag(Flags.Flag.SEEN, false);
        } catch (MessagingException e) {
            // TODO: Log me
            e.printStackTrace();
        }
    }


    public static void deleted(Message message) {
        try {
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
