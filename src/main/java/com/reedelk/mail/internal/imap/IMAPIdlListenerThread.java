package com.reedelk.mail.internal.imap;

import com.reedelk.mail.internal.exception.MailListenerException;
import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import static com.reedelk.mail.internal.commons.Messages.MailListenerComponent.IMAP_FOLDER_COULD_NOT_BE_OPENED_ERROR;
import static com.reedelk.mail.internal.commons.Messages.MailListenerComponent.IMAP_IDLE_COMMAND_ISSUE_ERROR;

public class IMAPIdlListenerThread extends Thread {

    private final Logger logger = LoggerFactory.getLogger(IMAPIdlListenerThread.class);

    private static final int ON_ERROR_SLEEP_TIME = 3000;

    private volatile boolean running = true;

    private final IMAPFolder folder;

    private final String username;
    private final String password;
    private final int folderOpenMode;

    public IMAPIdlListenerThread(String username, String password, IMAPFolder folder, int folderOpenMode) {
        this.folder = folder;
        this.username = username;
        this.password = password;
        this.folderOpenMode = folderOpenMode;
    }

    @Override
    public void run() {
        while (running) {

            try {

                ensureFolderOpen();

                folder.idle();

            } catch (Exception exception) {

                String message = IMAP_IDLE_COMMAND_ISSUE_ERROR.format(exception.getMessage());

                logger.warn(message);

                try {

                    Thread.sleep(ON_ERROR_SLEEP_TIME);

                } catch (InterruptedException interrupted) {

                    running = false;

                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void terminate() {
        running = false;
    }

    public void ensureFolderOpen() throws MessagingException {
        Store store = folder.getStore();
        if (store != null && !store.isConnected()) {
            store.connect(username, password);
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            folder.open(folderOpenMode);
            if (!folder.isOpen()) {
                String error = IMAP_FOLDER_COULD_NOT_BE_OPENED_ERROR.format(folder.getFullName());
                throw new MailListenerException(error);
            }
        }
    }
}
