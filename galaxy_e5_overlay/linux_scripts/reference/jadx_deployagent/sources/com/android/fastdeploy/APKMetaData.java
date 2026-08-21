package com.android.fastdeploy;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public final class APKMetaData extends GeneratedMessageLite<APKMetaData, Builder> implements APKMetaDataOrBuilder {
    public static final int ABSOLUTE_PATH_FIELD_NUMBER = 1;
    private static final APKMetaData DEFAULT_INSTANCE;
    public static final int ENTRIES_FIELD_NUMBER = 2;
    private static volatile Parser<APKMetaData> PARSER;
    private String absolutePath_ = "";
    private Internal.ProtobufList<APKEntry> entries_ = emptyProtobufList();

    private APKMetaData() {
    }

    @Override // com.android.fastdeploy.APKMetaDataOrBuilder
    public String getAbsolutePath() {
        return this.absolutePath_;
    }

    @Override // com.android.fastdeploy.APKMetaDataOrBuilder
    public ByteString getAbsolutePathBytes() {
        return ByteString.copyFromUtf8(this.absolutePath_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAbsolutePath(String value) {
        value.getClass();
        this.absolutePath_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearAbsolutePath() {
        this.absolutePath_ = getDefaultInstance().getAbsolutePath();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAbsolutePathBytes(ByteString value) {
        checkByteStringIsUtf8(value);
        this.absolutePath_ = value.toStringUtf8();
    }

    @Override // com.android.fastdeploy.APKMetaDataOrBuilder
    public List<APKEntry> getEntriesList() {
        return this.entries_;
    }

    public List<? extends APKEntryOrBuilder> getEntriesOrBuilderList() {
        return this.entries_;
    }

    @Override // com.android.fastdeploy.APKMetaDataOrBuilder
    public int getEntriesCount() {
        return this.entries_.size();
    }

    @Override // com.android.fastdeploy.APKMetaDataOrBuilder
    public APKEntry getEntries(int index) {
        return this.entries_.get(index);
    }

    public APKEntryOrBuilder getEntriesOrBuilder(int index) {
        return this.entries_.get(index);
    }

    private void ensureEntriesIsMutable() {
        Internal.ProtobufList<APKEntry> tmp = this.entries_;
        if (!tmp.isModifiable()) {
            this.entries_ = GeneratedMessageLite.mutableCopy(tmp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEntries(int index, APKEntry value) {
        value.getClass();
        ensureEntriesIsMutable();
        this.entries_.set(index, value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntries(APKEntry value) {
        value.getClass();
        ensureEntriesIsMutable();
        this.entries_.add(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntries(int index, APKEntry value) {
        value.getClass();
        ensureEntriesIsMutable();
        this.entries_.add(index, value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addAllEntries(Iterable<? extends APKEntry> values) {
        ensureEntriesIsMutable();
        AbstractMessageLite.addAll((Iterable) values, (List) this.entries_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearEntries() {
        this.entries_ = emptyProtobufList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeEntries(int index) {
        ensureEntriesIsMutable();
        this.entries_.remove(index);
    }

    public static APKMetaData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKMetaData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKMetaData parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKMetaData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKMetaData parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKMetaData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKMetaData parseFrom(InputStream input) throws IOException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKMetaData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKMetaData parseDelimitedFrom(InputStream input) throws IOException {
        return (APKMetaData) parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }

    public static APKMetaData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKMetaData) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKMetaData parseFrom(CodedInputStream input) throws IOException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKMetaData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKMetaData) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(APKMetaData prototype) {
        return DEFAULT_INSTANCE.createBuilder(prototype);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<APKMetaData, Builder> implements APKMetaDataOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 x0) {
            this();
        }

        private Builder() {
            super(APKMetaData.DEFAULT_INSTANCE);
        }

        @Override // com.android.fastdeploy.APKMetaDataOrBuilder
        public String getAbsolutePath() {
            return ((APKMetaData) this.instance).getAbsolutePath();
        }

        @Override // com.android.fastdeploy.APKMetaDataOrBuilder
        public ByteString getAbsolutePathBytes() {
            return ((APKMetaData) this.instance).getAbsolutePathBytes();
        }

        public Builder setAbsolutePath(String value) {
            copyOnWrite();
            ((APKMetaData) this.instance).setAbsolutePath(value);
            return this;
        }

        public Builder clearAbsolutePath() {
            copyOnWrite();
            ((APKMetaData) this.instance).clearAbsolutePath();
            return this;
        }

        public Builder setAbsolutePathBytes(ByteString value) {
            copyOnWrite();
            ((APKMetaData) this.instance).setAbsolutePathBytes(value);
            return this;
        }

        @Override // com.android.fastdeploy.APKMetaDataOrBuilder
        public List<APKEntry> getEntriesList() {
            return Collections.unmodifiableList(((APKMetaData) this.instance).getEntriesList());
        }

        @Override // com.android.fastdeploy.APKMetaDataOrBuilder
        public int getEntriesCount() {
            return ((APKMetaData) this.instance).getEntriesCount();
        }

        @Override // com.android.fastdeploy.APKMetaDataOrBuilder
        public APKEntry getEntries(int index) {
            return ((APKMetaData) this.instance).getEntries(index);
        }

        public Builder setEntries(int index, APKEntry value) {
            copyOnWrite();
            ((APKMetaData) this.instance).setEntries(index, value);
            return this;
        }

        public Builder setEntries(int index, APKEntry.Builder builderForValue) {
            copyOnWrite();
            ((APKMetaData) this.instance).setEntries(index, builderForValue.build());
            return this;
        }

        public Builder addEntries(APKEntry value) {
            copyOnWrite();
            ((APKMetaData) this.instance).addEntries(value);
            return this;
        }

        public Builder addEntries(int index, APKEntry value) {
            copyOnWrite();
            ((APKMetaData) this.instance).addEntries(index, value);
            return this;
        }

        public Builder addEntries(APKEntry.Builder builderForValue) {
            copyOnWrite();
            ((APKMetaData) this.instance).addEntries(builderForValue.build());
            return this;
        }

        public Builder addEntries(int index, APKEntry.Builder builderForValue) {
            copyOnWrite();
            ((APKMetaData) this.instance).addEntries(index, builderForValue.build());
            return this;
        }

        public Builder addAllEntries(Iterable<? extends APKEntry> values) {
            copyOnWrite();
            ((APKMetaData) this.instance).addAllEntries(values);
            return this;
        }

        public Builder clearEntries() {
            copyOnWrite();
            ((APKMetaData) this.instance).clearEntries();
            return this;
        }

        public Builder removeEntries(int index) {
            copyOnWrite();
            ((APKMetaData) this.instance).removeEntries(index);
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.fastdeploy.APKMetaData$1, reason: invalid class name */
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
                return new APKMetaData();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                Object[] objects = {"absolutePath_", "entries_", APKEntry.class};
                return newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0001\u0000\u0001Ȉ\u0002\u001b", objects);
            case 4:
                return DEFAULT_INSTANCE;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                Parser<APKMetaData> parser = PARSER;
                if (parser == null) {
                    synchronized (APKMetaData.class) {
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
        APKMetaData defaultInstance = new APKMetaData();
        DEFAULT_INSTANCE = defaultInstance;
        GeneratedMessageLite.registerDefaultInstance(APKMetaData.class, defaultInstance);
    }

    public static APKMetaData getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<APKMetaData> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
