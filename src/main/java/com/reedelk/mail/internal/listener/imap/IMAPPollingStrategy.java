package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMatcher;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;

import javax.mail.*;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;
import java.util.Optional;

import static javax.mail.Flags.Flag;

public class IMAPPollingStrategy extends AbstractPollingStrategy {

    private final IMAPMatcher matcher;
    private final IMAPConfiguration configuration;
    private final boolean batchEmails;
    private final boolean deleteOnSuccess;

    public IMAPPollingStrategy(InboundEventListener listener,
                               IMAPConfiguration configuration,
                               IMAPMatcher matcher,
                               Boolean deleteOnSuccess,
                               Boolean batchEmails) {
        super(listener);
        this.configuration = configuration;
        this.matcher = Optional.ofNullable(matcher).orElse(new IMAPMatcher());
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
    }

    @Override
    public void run() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = getFolder(store);
            folder.open(Folder.READ_WRITE);

            // search term to retrieve unseen messages from the folder
            // Apply matching flags ...
            SearchTerm searchTerm = createSearchTerm(matcher);
            Message[] messages = folder.search(searchTerm);

            for (Message message : messages) {
                // Process message if the processing was successful,
                // we set the flag according to the configured parameters.
                boolean success = processMessage(message);
                if (success) {
                    if (deleteOnSuccess) {
                        message.setFlag(Flag.DELETED, true);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: Log me
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            CloseableUtils.close(folder);
            CloseableUtils.close(store);
        }
    }

    private SearchTerm createSearchTerm(IMAPMatcher matcher) {
        FlagTerm seenFlag = new FlagTerm(new Flags(Flag.SEEN), getOrDefault(matcher.getSeen()));
        FlagTerm recentFlag = new FlagTerm(new Flags(Flag.RECENT), getOrDefault(matcher.getRecent()));
        FlagTerm answeredFlag = new FlagTerm(new Flags(Flag.ANSWERED), getOrDefault(matcher.getAnswered()));
        FlagTerm deletedFlag = new FlagTerm(new Flags(Flag.DELETED), getOrDefault(matcher.getDeleted()));
        return new AndTerm(new FlagTerm[] { seenFlag, recentFlag, answeredFlag, deletedFlag });
    }

    private boolean getOrDefault(Boolean value) {
        return value == null ? false : value;
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
