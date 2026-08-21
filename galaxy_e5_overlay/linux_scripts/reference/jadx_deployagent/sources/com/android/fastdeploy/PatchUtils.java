package com.android.fastdeploy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
class PatchUtils {
    public static final String SIGNATURE = "FASTDEPLOY";

    PatchUtils() {
    }

    static long readLELong(InputStream in) throws IOException {
        byte[] buffer = new byte[8];
        readFully(in, buffer);
        ByteBuffer buf = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
        return buf.getLong();
    }

    static String readString(InputStream in) throws IOException {
        int size = (int) readLELong(in);
        byte[] buffer = new byte[size];
        readFully(in, buffer);
        return new String(buffer);
    }

    static void readFully(InputStream in, byte[] destination, int startAt, int numBytes) throws IOException {
        int numRead = 0;
        while (numRead < numBytes) {
            int readNow = in.read(destination, startAt + numRead, numBytes - numRead);
            if (readNow == -1) {
                throw new IOException("truncated input stream");
            }
            numRead += readNow;
        }
    }

    static void readFully(InputStream in, byte[] destination) throws IOException {
        readFully(in, destination, 0, destination.length);
    }

    static void pipe(InputStream in, OutputStream out, byte[] buffer, long copyLength) throws IOException {
        while (copyLength > 0) {
            int maxCopy = (int) Math.min(buffer.length, copyLength);
            readFully(in, buffer, 0, maxCopy);
            out.write(buffer, 0, maxCopy);
            copyLength -= (long) maxCopy;
        }
    }
}
