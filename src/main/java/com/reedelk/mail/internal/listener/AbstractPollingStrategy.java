package com.reedelk.mail.internal.listener;

import com.reedelk.mail.component.ImapMailListener1;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.MailMessageToMessageMapper;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;
import javax.mail.search.FlagTerm;

public abstract class AbstractPollingStrategy implements ProtocolPollingStrategy {

    private final InboundEventListener listener;

    public AbstractPollingStrategy(InboundEventListener listener) {
        this.listener = listener;
    }

    protected abstract Store getStore() throws MessagingException;
    protected abstract Folder getFolder(Store store) throws MessagingException;

    @Override
    public void poll() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = getFolder(store);
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
                MailMessageToMessageMapper.map(ImapMailListener1.class, message);
        this.listener.onEvent(inMessage);
        // TODO: Call the listener. ... if process success, (the flow executed correctly)
        // Then ... otherwise wait...
        return true;
    }
}
