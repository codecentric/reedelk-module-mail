package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.MailListener;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.mail.internal.listener.PollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.ArrayList;
import java.util.List;

public class ImapPollingStrategy implements PollingStrategy {

    private final List<Integer> processMessageIds = new ArrayList<>();
    private final IMAPConfiguration configuration;
    private final InboundEventListener listener;

    public ImapPollingStrategy(IMAPConfiguration configuration, InboundEventListener listener) {
        this.configuration = configuration;
        this.listener = listener;
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
                    // double check the message is unseen
                    Message[] processMessage = folder.search(unseenFlagTerm, new Message[]{message});
                    if (processMessage != null && processMessage.length > 0L) {

                        // process message
                        boolean processed = processMessage(message);

                        if (processed) {
                            // update message seen flag
                            message.setFlag(Flags.Flag.SEEN, true);
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


    private boolean processMessage(Message message) throws Exception {
        com.reedelk.runtime.api.message.Message inMessage =
                MailMessageToMessageMapper.map(MailListener.class, message);
        this.listener.onEvent(inMessage);
        // TODO: Call the listener. ... if process success, (the flow executed correctly)
        // Then ... otherwise wait...
        return true;
    }
}
