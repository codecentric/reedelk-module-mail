package com.reedelk.mail.internal.listener;

public class IMAPPoller implements Poller {

    @Override
    public void poll() {
        /**
        Store store = null;
        Folder folder = null;
        try {
            Properties properties = new Properties();
            properties.put("mail.host", this.config.getHost());
            properties.put(EmailPollerConstants.PORT, this.config.getPort());
            properties.put(EmailPollerConstants.TLS_ENABLED, this.config.isTlsEnabled());

            // create session
            Session session = Session.getDefaultInstance(properties);
            store = session.getStore(this.config.getProtocol());

            // authenticate
            store.connect(this.config.getHost(), this.config.getUsername(), this.config.getPassword());

            // create the lookup folder and open with read and write access
            folder = store.getFolder(this.config.getLookupFolder());
            folder.open(Folder.READ_WRITE);

            // search term to retrieve unseen messages from the folder
            FlagTerm unseenFlagTerm = new FlagTerm(new Flags(Flag.SEEN), false);
            Message[] messages = folder.search(unseenFlagTerm);

            if (messages != null && messages.length > 0L) {
                for (Message message : messages) {

                    // check the message is processing
                    if (!this.processMessageIds.contains(message.getMessageNumber())) {
                        this.processMessageIds.add(message.getMessageNumber());

                        // double check the message is unseen
                        Message[] processMessage = folder.search(unseenFlagTerm, new Message[] { message });
                        if (processMessage != null && processMessage.length > 0L) {

                            // process message
                            boolean processed = processMessage(message);

                            if (processed) {
                                // update message seen flag
                                message.setFlag(Flag.SEEN, true);
                            }
                        }

                        // removing the processed message
                        this.processMessageIds.remove(message.getMessageNumber());
                    }
                }
            }

        } finally {
            if (folder != null)
                folder.close(false);

            if (store != null)
                store.close();
        }*/
    }

    @Override
    public void setListener(OnMessageListener listener) {

    }
}
