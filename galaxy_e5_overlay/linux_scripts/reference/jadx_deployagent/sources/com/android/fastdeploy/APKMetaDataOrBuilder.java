package com.android.fastdeploy;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;
import java.util.List;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public interface APKMetaDataOrBuilder extends MessageLiteOrBuilder {
    String getAbsolutePath();

    ByteString getAbsolutePathBytes();

    APKEntry getEntries(int i);

    int getEntriesCount();

    List<APKEntry> getEntriesList();
}
