package com.reedelk.mail.internal.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayUtils {

    public static byte[] from(final InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
