package com.reedelk.mail.internal.listener.imap;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.Closeable;

// TODO: This one can be improved as well.
public class IMAPIdlListenerThread extends Thread implements Closeable {

    private volatile boolean running = true;

    private final Folder folder;
    private final String host;
    private final String username;
    private final String password;

    public IMAPIdlListenerThread(String host, String username, String password, Folder folder) {
        this.folder = folder;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        while (running) {
            try {
                ensureOpen(folder);
                ((IMAPFolder) folder).idle();
            } catch (Exception e) {
                // something went wrong
                // wait and try again
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }

        }
    }

    public void ensureOpen(final Folder folder) throws MessagingException {
        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(host, username, password);
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }

    }

    @Override
    public synchronized void close() {
        if (!running) {
            return;
        }
        running = false;
    }
}
