package com.reedelk.mail.internal.commons;

public class Defaults {

    public static final int CONNECT_TIMEOUT = 60000;
    public static final int SOCKET_TIMEOUT = 30000;
    public static final boolean TLS_ENABLE = false;

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
}
