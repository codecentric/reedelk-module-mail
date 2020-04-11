package com.reedelk.mail.internal.commons;

public class Defaults {

    public static final String UNKNOWN_ATTACHMENT_MIME_EXTENSION = "dat";

    public static final int FLOW_MAX_MESSAGE_TIMEOUT_SECONDS = 120;
    public static final int CONNECT_TIMEOUT = 180000;
    public static final int SOCKET_TIMEOUT = 60000;
    public static final boolean TLS_ENABLE = false;
    public static final boolean PEEK = false;
    public static final String IMAP_FOLDER_NAME = "INBOX";
    public static final String POP_FOLDER_NAME = "INBOX";

    public static class SMTP {
        public static final String TRANSPORT = "smtp";
        public static final int DEFAULT_PORT = 25;
    }

    public static class SMTPs {
        public static final String TRANSPORT = "smtps";
        public static final int DEFAULT_PORT = 465;
    }

    public static class POP3 {
        public static final String TRANSPORT = "pop3";
        public static final int DEFAULT_PORT = 110;
    }

    public static class POP3s {
        public static final String TRANSPORT = "pop3s";
        public static final int DEFAULT_PORT = 995;
    }

    public static class IMAP {
        public static final String TRANSPORT = "imap";
        public static final int DEFAULT_PORT = 143;
    }

    public static class IMAPs {
        public static final String TRANSPORT = "imaps";
        public static final int DEFAULT_PORT = 993;
    }

    public static class Poller {
        public static final int DEFAULT_POLL_INTERVAL = 120000;
        public static final int LIMIT = 10;
        public static final boolean MARK_DELETE_ON_SUCCESS = false;
        public static final boolean DELETE_ON_SUCCESS = false;
        public static final boolean BATCH_EMAILS = false;
    }
}
