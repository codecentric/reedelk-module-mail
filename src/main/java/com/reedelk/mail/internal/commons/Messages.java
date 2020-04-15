package com.reedelk.mail.internal.commons;

import com.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum MailListenerComponent implements FormattedMessage {

        IDLE_SETUP_ERROR("Could not open IMAP connection with strategy IDLE, error=[%s]."),
        IMAP_IDLE_CAPABILITY_NOT_SUPPORTED("The IMAP server does not support IDLE capability. Use POLLING strategy instead."),
        IMAP_FOLDER_COULD_NOT_BE_OPENED_ERROR("IMAP folder named=[%s] could not be opened."),
        IMAP_IDLE_COMMAND_ISSUE_ERROR("Could not perform IDLE command on IMAP server due to error=[%s]. I will try again shortly."),
        FLAG_SET_ERROR("Could not set flag=[%s] to value=[%s], error=[%s]."),
        POLL_ERROR("Could not poll mail messages, error=[%s].");

        private String message;

        MailListenerComponent(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum MailSendComponent implements FormattedMessage {

        MAIL_BODY_EMPTY_ERROR("The mail body must not be empty"),
        MAIL_MESSAGE_ERROR("The Mail message could not be built, error=[%s]"),
        ATTACHMENT_FILE_NAME("The attachment file name must not be empty (expression=[%s])"),
        ATTACHMENT_FILE_EMPTY("The attachment file from expression=[%s] is empty"),
        ATTACHMENT_FILE_NAME_EMPTY("The attachment file name from expression=[%s] is empty"),
        ATTACHMENT_RESOURCE_MUST_NOT_BE_EMPTY("The attachment resource file must not be empty for attachment source type 'Resource'"),
        FROM_ERROR("'from' address must not be empty (expression=[%s])"),
        TO_ERROR("'to' addresses must not be empty (expression=[%s])"),
        CC_ERROR("Could not evaluate 'cc' addresses=[%s] (expression=[%s])"),
        BCC_ERROR("Could not evaluate 'bcc' addresses=[%s] (expression=[%s])"),
        SUBJECT_ERROR("Could not evaluate 'Subject' message=[%s] (expression=[%s])"),
        REPLY_TO_ERROR("Could not evaluate 'ReplyTo' addresses=[%s] (expression=[%s])");

        private String message;

        MailSendComponent(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
