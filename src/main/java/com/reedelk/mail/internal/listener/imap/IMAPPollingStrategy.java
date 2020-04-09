package com.reedelk.mail.internal.listener.imap;

import com.reedelk.mail.component.IMAPConfiguration;
import com.reedelk.mail.component.IMAPMailListener;
import com.reedelk.mail.component.IMAPMatcher;
import com.reedelk.mail.internal.commons.CloseableUtils;
import com.reedelk.mail.internal.commons.Defaults;
import com.reedelk.mail.internal.listener.AbstractPollingStrategy;
import com.reedelk.mail.internal.properties.IMAPProperties;
import com.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.search.AndTerm;
import javax.mail.search.SearchTerm;
import java.util.Optional;

import static javax.mail.Flags.Flag;

public class IMAPPollingStrategy extends AbstractPollingStrategy {

    private final Logger logger = LoggerFactory.getLogger(IMAPPollingStrategy.class);

    private final IMAPMatcher matcher;
    private final IMAPConfiguration configuration;

    private final String inboxFolder;
    private final boolean batchEmails;
    private final boolean deleteOnSuccess;
    private final boolean markDeletedOnSuccess;
    private final Integer limit;

    public IMAPPollingStrategy(InboundEventListener listener,
                               IMAPConfiguration configuration,
                               IMAPMatcher matcher,
                               Boolean deleteOnSuccess,
                               Boolean markDeletedOnSuccess,
                               Boolean batchEmails,
                               Integer limit) {
        super(listener);
        this.limit = limit;
        this.configuration = configuration;
        this.matcher = Optional.ofNullable(matcher).orElse(new IMAPMatcher());
        this.batchEmails = Optional.ofNullable(batchEmails).orElse(Defaults.Poller.BATCH_EMAILS);
        this.deleteOnSuccess = Optional.ofNullable(deleteOnSuccess).orElse(Defaults.Poller.DELETE_ON_SUCCESS);
        this.markDeletedOnSuccess = Optional.ofNullable(markDeletedOnSuccess).orElse(Defaults.Poller.MARK_DELETED_ON_SUCCESS);
        this.inboxFolder = Optional.ofNullable(configuration.getFolder()).orElse(Defaults.IMAP_FOLDER_NAME);
    }

    @Override
    public void run() {
        Store store = null;
        Folder folder = null;
        try {
            store = getStore();
            folder = store.getFolder(inboxFolder);
            folder.open(Folder.READ_WRITE);

            if (Thread.interrupted()) return;

            SearchTerm searchTerm = createSearchTerm(matcher);

            Message[] messages = folder.search(searchTerm);

            if (Thread.interrupted()) return;

            Message[] toProcess = messages;
            if (limit != null) {
                toProcess = new Message[limit];
                if (limit >= 0) System.arraycopy(messages, 0, toProcess, 0, limit);
            }

            if (batchEmails) {
                boolean success = processMessages(IMAPMailListener.class, toProcess);
                if (success) applyMessagesOnSuccessFlags(toProcess);

            } else {
                for (Message message : toProcess) {
                    if (Thread.interrupted()) return;
                    // Process each message one at a time. If the processing was successful,
                    // then we apply the flags to the message (e.g marking it deleted)
                    boolean success = processMessage(IMAPMailListener.class, message);
                    if (success) applyMessageOnSuccessFlags(message);
                }
            }

        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);

        } finally {
            CloseableUtils.close(folder, deleteOnSuccess);
            CloseableUtils.close(store);
        }
    }

    private void applyMessageOnSuccessFlags(Message message) throws MessagingException {
        if (deleteOnSuccess || markDeletedOnSuccess) {
            message.setFlag(Flag.DELETED, true);
        }
    }

    private void applyMessagesOnSuccessFlags(Message[] messages) throws MessagingException {
        for (Message message : messages) {
            applyMessageOnSuccessFlags(message);
        }
    }

    private SearchTerm createSearchTerm(IMAPMatcher matcher) {
        SearchTerm seenFlag = matcher.getSeen().searchTermOf(Flag.SEEN);
        SearchTerm recentFlag = matcher.getRecent().searchTermOf(Flag.RECENT);
        SearchTerm answeredFlag = matcher.getAnswered().searchTermOf(Flag.ANSWERED);
        SearchTerm deletedFlag = matcher.getDeleted().searchTermOf(Flag.DELETED);
        return new AndTerm(new SearchTerm[]{seenFlag, recentFlag, answeredFlag, deletedFlag});
    }

    private Store getStore() throws MessagingException {
        Session session = Session.getDefaultInstance(new IMAPProperties(configuration));
        Store store = session.getStore();
        store.connect(configuration.getHost(), configuration.getUsername(), configuration.getPassword());
        return store;
    }
}
