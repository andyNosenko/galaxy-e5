package com.android.fastdeploy;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public final class APKEntry extends GeneratedMessageLite<APKEntry, Builder> implements APKEntryOrBuilder {
    public static final int DATAOFFSET_FIELD_NUMBER = 2;
    public static final int DATASIZE_FIELD_NUMBER = 3;
    private static final APKEntry DEFAULT_INSTANCE;
    public static final int MD5_FIELD_NUMBER = 1;
    private static volatile Parser<APKEntry> PARSER;
    private long dataOffset_;
    private long dataSize_;
    private ByteString md5_ = ByteString.EMPTY;

    private APKEntry() {
    }

    @Override // com.android.fastdeploy.APKEntryOrBuilder
    public ByteString getMd5() {
        return this.md5_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMd5(ByteString value) {
        value.getClass();
        this.md5_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearMd5() {
        this.md5_ = getDefaultInstance().getMd5();
    }

    @Override // com.android.fastdeploy.APKEntryOrBuilder
    public long getDataOffset() {
        return this.dataOffset_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDataOffset(long value) {
        this.dataOffset_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearDataOffset() {
        this.dataOffset_ = 0L;
    }

    @Override // com.android.fastdeploy.APKEntryOrBuilder
    public long getDataSize() {
        return this.dataSize_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDataSize(long value) {
        this.dataSize_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearDataSize() {
        this.dataSize_ = 0L;
    }

    public static APKEntry parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKEntry parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKEntry parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKEntry parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKEntry parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKEntry parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKEntry parseFrom(InputStream input) throws IOException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKEntry parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKEntry parseDelimitedFrom(InputStream input) throws IOException {
        return (APKEntry) parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }

    public static APKEntry parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKEntry) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKEntry parseFrom(CodedInputStream input) throws IOException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKEntry parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKEntry) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(APKEntry prototype) {
        return DEFAULT_INSTANCE.createBuilder(prototype);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<APKEntry, Builder> implements APKEntryOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 x0) {
            this();
        }

        private Builder() {
            super(APKEntry.DEFAULT_INSTANCE);
        }

        @Override // com.android.fastdeploy.APKEntryOrBuilder
        public ByteString getMd5() {
            return ((APKEntry) this.instance).getMd5();
        }

        public Builder setMd5(ByteString value) {
            copyOnWrite();
            ((APKEntry) this.instance).setMd5(value);
            return this;
        }

        public Builder clearMd5() {
            copyOnWrite();
            ((APKEntry) this.instance).clearMd5();
            return this;
        }

        @Override // com.android.fastdeploy.APKEntryOrBuilder
        public long getDataOffset() {
            return ((APKEntry) this.instance).getDataOffset();
        }

        public Builder setDataOffset(long value) {
            copyOnWrite();
            ((APKEntry) this.instance).setDataOffset(value);
            return this;
        }

        public Builder clearDataOffset() {
            copyOnWrite();
            ((APKEntry) this.instance).clearDataOffset();
            return this;
        }

        @Override // com.android.fastdeploy.APKEntryOrBuilder
        public long getDataSize() {
            return ((APKEntry) this.instance).getDataSize();
        }

        public Builder setDataSize(long value) {
            copyOnWrite();
            ((APKEntry) this.instance).setDataSize(value);
            return this;
        }

        public Builder clearDataSize() {
            copyOnWrite();
            ((APKEntry) this.instance).clearDataSize();
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.fastdeploy.APKEntry$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = new int[GeneratedMessageLite.MethodToInvoke.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    @Override // com.google.protobuf.GeneratedMessageLite
    protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
        AnonymousClass1 anonymousClass1 = null;
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[method.ordinal()]) {
            case 1:
                return new APKEntry();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                Object[] objects = {"md5_", "dataOffset_", "dataSize_"};
                return newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\n\u0002\u0002\u0003\u0002", objects);
            case 4:
                return DEFAULT_INSTANCE;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                Parser<APKEntry> parser = PARSER;
                if (parser == null) {
                    synchronized (APKEntry.class) {
                        parser = PARSER;
                        if (parser == null) {
                            parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                            PARSER = parser;
                        }
                        break;
                    }
                }
                return parser;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        APKEntry defaultInstance = new APKEntry();
        DEFAULT_INSTANCE = defaultInstance;
        GeneratedMessageLite.registerDefaultInstance(APKEntry.class, defaultInstance);
    }

    public static APKEntry getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<APKEntry> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
