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
public final class APKDump extends GeneratedMessageLite<APKDump, Builder> implements APKDumpOrBuilder {
    public static final int ABSOLUTE_PATH_FIELD_NUMBER = 4;
    public static final int CD_FIELD_NUMBER = 2;
    private static final APKDump DEFAULT_INSTANCE;
    public static final int NAME_FIELD_NUMBER = 1;
    private static volatile Parser<APKDump> PARSER = null;
    public static final int SIGNATURE_FIELD_NUMBER = 3;
    private String name_ = "";
    private ByteString cd_ = ByteString.EMPTY;
    private ByteString signature_ = ByteString.EMPTY;
    private String absolutePath_ = "";

    private APKDump() {
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
    public String getName() {
        return this.name_;
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
    public ByteString getNameBytes() {
        return ByteString.copyFromUtf8(this.name_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setName(String value) {
        value.getClass();
        this.name_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearName() {
        this.name_ = getDefaultInstance().getName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNameBytes(ByteString value) {
        checkByteStringIsUtf8(value);
        this.name_ = value.toStringUtf8();
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
    public ByteString getCd() {
        return this.cd_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCd(ByteString value) {
        value.getClass();
        this.cd_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCd() {
        this.cd_ = getDefaultInstance().getCd();
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
    public ByteString getSignature() {
        return this.signature_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSignature(ByteString value) {
        value.getClass();
        this.signature_ = value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearSignature() {
        this.signature_ = getDefaultInstance().getSignature();
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
    public String getAbsolutePath() {
        return this.absolutePath_;
    }

    @Override // com.android.fastdeploy.APKDumpOrBuilder
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

    public static APKDump parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKDump parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKDump parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKDump parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKDump parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
    }

    public static APKDump parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
    }

    public static APKDump parseFrom(InputStream input) throws IOException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKDump parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKDump parseDelimitedFrom(InputStream input) throws IOException {
        return (APKDump) parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }

    public static APKDump parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKDump) parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static APKDump parseFrom(CodedInputStream input) throws IOException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
    }

    public static APKDump parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return (APKDump) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(APKDump prototype) {
        return DEFAULT_INSTANCE.createBuilder(prototype);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<APKDump, Builder> implements APKDumpOrBuilder {
        /* synthetic */ Builder(AnonymousClass1 x0) {
            this();
        }

        private Builder() {
            super(APKDump.DEFAULT_INSTANCE);
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public String getName() {
            return ((APKDump) this.instance).getName();
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public ByteString getNameBytes() {
            return ((APKDump) this.instance).getNameBytes();
        }

        public Builder setName(String value) {
            copyOnWrite();
            ((APKDump) this.instance).setName(value);
            return this;
        }

        public Builder clearName() {
            copyOnWrite();
            ((APKDump) this.instance).clearName();
            return this;
        }

        public Builder setNameBytes(ByteString value) {
            copyOnWrite();
            ((APKDump) this.instance).setNameBytes(value);
            return this;
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public ByteString getCd() {
            return ((APKDump) this.instance).getCd();
        }

        public Builder setCd(ByteString value) {
            copyOnWrite();
            ((APKDump) this.instance).setCd(value);
            return this;
        }

        public Builder clearCd() {
            copyOnWrite();
            ((APKDump) this.instance).clearCd();
            return this;
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public ByteString getSignature() {
            return ((APKDump) this.instance).getSignature();
        }

        public Builder setSignature(ByteString value) {
            copyOnWrite();
            ((APKDump) this.instance).setSignature(value);
            return this;
        }

        public Builder clearSignature() {
            copyOnWrite();
            ((APKDump) this.instance).clearSignature();
            return this;
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public String getAbsolutePath() {
            return ((APKDump) this.instance).getAbsolutePath();
        }

        @Override // com.android.fastdeploy.APKDumpOrBuilder
        public ByteString getAbsolutePathBytes() {
            return ((APKDump) this.instance).getAbsolutePathBytes();
        }

        public Builder setAbsolutePath(String value) {
            copyOnWrite();
            ((APKDump) this.instance).setAbsolutePath(value);
            return this;
        }

        public Builder clearAbsolutePath() {
            copyOnWrite();
            ((APKDump) this.instance).clearAbsolutePath();
            return this;
        }

        public Builder setAbsolutePathBytes(ByteString value) {
            copyOnWrite();
            ((APKDump) this.instance).setAbsolutePathBytes(value);
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.fastdeploy.APKDump$1, reason: invalid class name */
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
                return new APKDump();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                Object[] objects = {"name_", "cd_", "signature_", "absolutePath_"};
                return newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0004\u0000\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001Ȉ\u0002\n\u0003\n\u0004Ȉ", objects);
            case 4:
                return DEFAULT_INSTANCE;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                Parser<APKDump> parser = PARSER;
                if (parser == null) {
                    synchronized (APKDump.class) {
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
        APKDump defaultInstance = new APKDump();
        DEFAULT_INSTANCE = defaultInstance;
        GeneratedMessageLite.registerDefaultInstance(APKDump.class, defaultInstance);
    }

    public static APKDump getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<APKDump> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
