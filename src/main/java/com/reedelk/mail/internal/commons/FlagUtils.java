package com.reedelk.mail.internal.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import static com.reedelk.mail.internal.commons.Messages.MailListenerComponent.FLAG_SET_ERROR;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FlagUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlagUtils.class);

    public static void notSeen(Message message) {
        try {
            message.setFlag(Flags.Flag.SEEN, false);
        } catch (MessagingException exception) {
            String error = FLAG_SET_ERROR.format("SEEN", FALSE.toString(), exception.getMessage());
            logger.error(error, exception);
        }
    }


    public static void deleted(Message message) {
        try {
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException exception) {
            String error = FLAG_SET_ERROR.format("DELETED", TRUE.toString(), exception.getMessage());
            logger.error(error, exception);
        }
    }
}
