package com.google.protobuf;

import java.nio.Buffer;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
final class Java8Compatibility {
    static void clear(Buffer b) {
        b.clear();
    }

    static void flip(Buffer b) {
        b.flip();
    }

    static void limit(Buffer b, int limit) {
        b.limit(limit);
    }

    static void mark(Buffer b) {
        b.mark();
    }

    static void position(Buffer b, int position) {
        b.position(position);
    }

    static void reset(Buffer b) {
        b.reset();
    }

    private Java8Compatibility() {
    }
}
