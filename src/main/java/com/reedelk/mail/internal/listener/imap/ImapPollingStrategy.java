package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;

public class ImapPollingStrategy extends AbstractPollingStrategy {

    private final IMAPConfiguration configuration;

    public ImapPollingStrategy(IMAPConfiguration configuration, InboundEventListener listener) {
        super(listener, false); // TODO: Fixme
        this.configuration = configuration;
    }

    @Override
    public void poll() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = getFolder(store);
            folder.open(Folder.READ_WRITE);

            // search term to retrieve unseen messages from the folder


            FromStringTerm fromStringTerm = new FromStringTerm("info@reedelk.com");
            FlagTerm unseenFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = folder.search(unseenFlagTerm);

            if (messages != null && messages.length > 0L) {
                for (Message message : messages) {
                    // double check the message is unseen
                    Message[] processMessage = folder.search(unseenFlagTerm, new Message[]{message});
                    if (processMessage != null && processMessage.length > 0L) {

                        // process message
                        boolean processed = processMessage(message);

                        if (processed) {
                            // update message seen flag
                            message.setFlag(Flags.Flag.SEEN, true);

                            if (deleteAfterRetrieve) {
                                message.setFlag(Flags.Flag.DELETED, true);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            CloseableUtils.close(folder);
            CloseableUtils.close(store);
        }
    }

    private Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new IMAPProperties(configuration));
        Store store = session.getStore();
        // TODO: If authenticate then connect with authentication.
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }


    private Folder getFolder(Store store) throws MessagingException {
        return store.getFolder(configuration.getFolder());
    }
}
