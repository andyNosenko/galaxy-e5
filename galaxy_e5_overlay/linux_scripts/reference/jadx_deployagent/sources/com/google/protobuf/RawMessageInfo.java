package com.google.protobuf;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
@CheckReturnValue
final class RawMessageInfo implements MessageInfo {
    private static final int IS_EDITION_BIT = 4;
    private static final int IS_PROTO2_BIT = 1;
    private final MessageLite defaultInstance;
    private final int flags;
    private final String info;
    private final Object[] objects;

    RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
        this.defaultInstance = defaultInstance;
        this.info = info;
        this.objects = objects;
        int position = 0 + 1;
        int value = info.charAt(0);
        if (value < 55296) {
            this.flags = value;
            return;
        }
        int result = value & 8191;
        int shift = 13;
        while (true) {
            int position2 = position + 1;
            int value2 = info.charAt(position);
            if (value2 >= 55296) {
                result |= (value2 & 8191) << shift;
                shift += 13;
                position = position2;
            } else {
                this.flags = (value2 << shift) | result;
                return;
            }
        }
    }

    String getStringInfo() {
        return this.info;
    }

    Object[] getObjects() {
        return this.objects;
    }

    @Override // com.google.protobuf.MessageInfo
    public MessageLite getDefaultInstance() {
        return this.defaultInstance;
    }

    @Override // com.google.protobuf.MessageInfo
    public ProtoSyntax getSyntax() {
        if ((this.flags & 1) != 0) {
            return ProtoSyntax.PROTO2;
        }
        if ((this.flags & 4) == 4) {
            return ProtoSyntax.EDITIONS;
        }
        return ProtoSyntax.PROTO3;
    }

    @Override // com.google.protobuf.MessageInfo
    public boolean isMessageSetWireFormat() {
        return (this.flags & 2) == 2;
    }
}
