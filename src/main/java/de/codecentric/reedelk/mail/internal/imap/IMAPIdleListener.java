package de.codecentric.reedelk.mail.internal.imap;

import de.codecentric.reedelk.mail.component.IMAPConfiguration;
import de.codecentric.reedelk.mail.component.IMAPMailListener;
import de.codecentric.reedelk.mail.internal.commons.CloseableUtils;
import de.codecentric.reedelk.mail.internal.exception.MailListenerException;
import de.codecentric.reedelk.runtime.api.exception.PlatformException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.event.MessageCountListener;
import java.io.Closeable;

import static de.codecentric.reedelk.mail.internal.commons.Messages.MailListenerComponent.IDLE_SETUP_ERROR;
import static de.codecentric.reedelk.mail.internal.commons.Messages.MailListenerComponent.IMAP_IDLE_CAPABILITY_NOT_SUPPORTED;

public class IMAPIdleListener implements Closeable {

    private static final String IDLE_CAPABILITY = "IDLE";

    private final IMAPIdleListenerSettings settings;

    private IMAPStore store;
    private IMAPFolder folder;
    private IMAPIdlListenerThread listenerThread;
    private MessageCountListener messageCountListener;

    public IMAPIdleListener(IMAPMailListener listener, IMAPIdleListenerSettings settings) {
        this.settings = settings;
        messageCountListener = new IMAPIdleMessageAdapter(listener, settings);
    }

    public void start() {

        IMAPConfiguration configuration = settings.getConfiguration();

        String username = configuration.getUsername();

        String password = configuration.getPassword();

        Session session = Session.getInstance(new IMAPProperties(configuration));

        int folderOpenMode = Folder.READ_WRITE;

        try {
            store = (IMAPStore) session.getStore();

            store.connect(username, password);

            checkIdleCapabilityOrThrow();

            folder = (IMAPFolder) store.getFolder(settings.getFolder());

        } catch (Exception exception) {

            // We must clean up partially opened resources such as store and folder.
            cleanup();

            String error = IDLE_SETUP_ERROR.format(exception.getMessage());

            throw new MailListenerException(error, exception);
        }

        folder.addMessageCountListener(messageCountListener);

        listenerThread = new IMAPIdlListenerThread(username, password, folder, folderOpenMode);

        listenerThread.start();

    }

    @Override
    public void close() {
        cleanup();
    }

    private void cleanup() {
        if (listenerThread != null) {
            listenerThread.terminate();
        }

        CloseableUtils.close(folder);
        CloseableUtils.close(store);

        if (listenerThread != null) {
            try {
                listenerThread.join();
            } catch (InterruptedException exception) {
                // nothing we can do about this exception,
                // we restore the interruption status.
                Thread.currentThread().interrupt();
            }
        }
    }

    private void checkIdleCapabilityOrThrow() throws MessagingException {
        if (!store.hasCapability(IDLE_CAPABILITY)) {
            String error = IMAP_IDLE_CAPABILITY_NOT_SUPPORTED.format();
            throw new PlatformException(error);
        }
    }
}
