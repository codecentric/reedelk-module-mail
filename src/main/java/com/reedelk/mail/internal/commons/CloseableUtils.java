package com.reedelk.mail.internal.commons;

import javax.mail.Folder;
import javax.mail.Store;
import java.io.Closeable;
import java.io.IOException;

public class CloseableUtils {

    public static void close(final Store store) {
        if (store == null) return;
        if (store.isConnected()) {
            try {
                store.close();
            } catch (final Exception e) {
                // ignore
            }
        }
    }

    public static void close(Folder folder) {
        if (folder == null) return;
        if (folder.isOpen()) {
            try {
                folder.close();
            } catch (final Exception e) {
                // ignore
            }
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
