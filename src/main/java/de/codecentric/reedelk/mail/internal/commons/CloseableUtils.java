package de.codecentric.reedelk.mail.internal.commons;

import javax.mail.Folder;
import javax.mail.Store;

public class CloseableUtils {

    private CloseableUtils() {
    }

    public static void close(final Store store) {
        if (store == null) return;
        if (!store.isConnected()) return;
        try {
            store.close();
        } catch (Exception e) {
            // ignore
        }
    }

    public static void close(Folder folder) {
        if (folder == null) return;
        if (!folder.isOpen()) return;
        try {
            folder.close(false);
        } catch (Exception e) {
            // ignore
        }
    }

    public static void close(Folder folder, Boolean expunge) {
        if (folder == null) return;
        if (!folder.isOpen()) return;
        try {
            folder.close(expunge);
        } catch (Exception e) {
            // ignore
        }
    }
}
