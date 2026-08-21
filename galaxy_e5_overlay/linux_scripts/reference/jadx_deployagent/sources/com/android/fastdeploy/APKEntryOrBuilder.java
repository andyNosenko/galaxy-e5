package com.android.fastdeploy;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public interface APKEntryOrBuilder extends MessageLiteOrBuilder {
    long getDataOffset();

    long getDataSize();

    ByteString getMd5();
}
