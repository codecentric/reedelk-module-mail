package com.reedelk.mail.internal.commons;

public class Messages {

    private Messages() {
    }

    private static String formatMessage(String template, Object ...args) {
        return String.format(template, args);
    }

    interface FormattedMessage {
        String format(Object ...args);
    }

    public enum MailSendComponent implements FormattedMessage {

        MAIL_MESSAGE_ERROR("The Mail message could not be built, error=[%s]"),
        FROM_ERROR("'from' address must not be empty (expression=[%s])"),
        TO_ERROR("'to' addresses must not be empty (expression=[%s])"),
        CC_ERROR("Could not evaluate 'cc' addresses=[%s] (expression=[%s])"),
        BCC_ERROR("Could not evaluate 'bcc' addresses=[%s] (expression=[%s])"),
        REPLY_TO_ERROR("Could not evaluate 'ReplyTo' addresses=[%s] (expression=[%s])"),
        SUBJECT_ERROR("Could not evaluate 'Subject' message=[%s] (expression=[%s])");

        private String msg;

        MailSendComponent(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }
}
