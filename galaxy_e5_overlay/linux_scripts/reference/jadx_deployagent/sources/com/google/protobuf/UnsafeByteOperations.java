package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public final class UnsafeByteOperations {
    private UnsafeByteOperations() {
    }

    public static ByteString unsafeWrap(byte[] buffer) {
        return ByteString.wrap(buffer);
    }

    public static ByteString unsafeWrap(byte[] buffer, int offset, int length) {
        return ByteString.wrap(buffer, offset, length);
    }

    public static ByteString unsafeWrap(ByteBuffer buffer) {
        return ByteString.wrap(buffer);
    }

    public static void unsafeWriteTo(ByteString bytes, ByteOutput output) throws IOException {
        bytes.writeTo(output);
    }
}
