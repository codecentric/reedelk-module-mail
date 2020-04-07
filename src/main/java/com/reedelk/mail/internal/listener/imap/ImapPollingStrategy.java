package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.listener.OnMessageListener;
import com.reedelk.mail.internal.listener.PollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.ArrayList;
import java.util.List;

public class ImapPollingStrategy implements PollingStrategy {

    private final List<Integer> processMessageIds = new ArrayList<>();
    private final IMAPConfiguration configuration;

    public ImapPollingStrategy(IMAPConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void poll() {
        Store store = null;
        Folder folder = null;

        try {
            Session session = Session.getDefaultInstance(new IMAPProperties(configuration));
            store = session.getStore();

            // authenticate
            // TODO: If authenticate then connect with authentication.
            store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());

            // create the lookup folder and open with read and write access
            folder = store.getFolder(configuration.getFolder());
            folder.open(Folder.READ_WRITE);

            // search term to retrieve unseen messages from the folder
            FlagTerm unseenFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
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
                                message.setFlag(Flags.Flag.SEEN, true);
                            }
                        }

                        // removing the processed message
                        this.processMessageIds.remove(message.getMessageNumber());
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


    private boolean processMessage(Message message) throws Exception {
        // TODO: Call the listener. ... if process success, (the flow executed correctly)
        // Then ... otherwise wait...
        return true;
    }

    @Override
    public void setListener(OnMessageListener listener) {

    }
}
