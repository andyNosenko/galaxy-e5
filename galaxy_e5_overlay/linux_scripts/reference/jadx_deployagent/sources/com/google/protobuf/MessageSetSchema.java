package com.google.protobuf;

import java.io.IOException;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
@CheckReturnValue
final class MessageSetSchema<T> implements Schema<T> {
    private final MessageLite defaultInstance;
    private final ExtensionSchema<?> extensionSchema;
    private final boolean hasExtensions;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;

    private MessageSetSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        this.unknownFieldSchema = unknownFieldSchema;
        this.hasExtensions = extensionSchema.hasExtensions(defaultInstance);
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
    }

    static <T> MessageSetSchema<T> newSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
        return new MessageSetSchema<>(unknownFieldSchema, extensionSchema, defaultInstance);
    }

    @Override // com.google.protobuf.Schema
    public T newInstance() {
        if (this.defaultInstance instanceof GeneratedMessageLite) {
            return (T) ((GeneratedMessageLite) this.defaultInstance).newMutableInstance();
        }
        return (T) this.defaultInstance.newBuilderForType().buildPartial();
    }

    @Override // com.google.protobuf.Schema
    public boolean equals(T message, T other) {
        Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
        Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
        if (!messageUnknown.equals(otherUnknown)) {
            return false;
        }
        if (this.hasExtensions) {
            return this.extensionSchema.getExtensions(message).equals(this.extensionSchema.getExtensions(other));
        }
        return true;
    }

    @Override // com.google.protobuf.Schema
    public int hashCode(T message) {
        int hashCode = this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            return (hashCode * 53) + this.extensionSchema.getExtensions(message).hashCode();
        }
        return hashCode;
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T message, T other) {
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    @Override // com.google.protobuf.Schema
    public void writeTo(T message, Writer writer) throws IOException {
        for (T extension : this.extensionSchema.getExtensions(message)) {
            FieldSet.FieldDescriptorLite<?> fd = (FieldSet.FieldDescriptorLite) extension.getKey();
            if (fd.getLiteJavaType() != WireFormat.JavaType.MESSAGE || fd.isRepeated() || fd.isPacked()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (extension instanceof LazyField.LazyEntry) {
                writer.writeMessageSetItem(fd.getNumber(), ((LazyField.LazyEntry) extension).getField().toByteString());
            } else {
                writer.writeMessageSetItem(fd.getNumber(), extension.getValue());
            }
        }
        writeUnknownFieldsHelper(this.unknownFieldSchema, message, writer);
    }

    private <UT, UB> void writeUnknownFieldsHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, T message, Writer writer) throws IOException {
        unknownFieldSchema.writeAsMessageSetTo(unknownFieldSchema.getFromMessage(message), writer);
    }

    /* JADX WARN: Code duplicated, block: B:34:0x00ec  */
    /* JADX WARN: Code duplicated, block: B:56:0x00ea A[SYNTHETIC] */
    /* JADX WARN: Failed to find 'out' block for switch in B:20:0x0093. Please report as an issue. */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.Schema
    public void mergeFrom(T t, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        UnknownFieldSetLite unknownFields;
        UnknownFieldSetLite unknownFields2 = ((GeneratedMessageLite) t).unknownFields;
        if (unknownFields2 != UnknownFieldSetLite.getDefaultInstance()) {
            unknownFields = unknownFields2;
        } else {
            UnknownFieldSetLite unknownFields3 = UnknownFieldSetLite.newInstance();
            ((GeneratedMessageLite) t).unknownFields = unknownFields3;
            unknownFields = unknownFields3;
        }
        FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage) t).ensureExtensionsAreMutable();
        GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;
        int position2 = position;
        while (position2 < limit) {
            GeneratedMessageLite.GeneratedExtension<?, ?> extension2 = extension;
            int position3 = ArrayDecoders.decodeVarint32(data, position2, registers);
            int startTag = registers.int1;
            if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
                if (WireFormat.getTagWireType(startTag) == 2) {
                    GeneratedMessageLite.GeneratedExtension<?, ?> extension3 = (GeneratedMessageLite.GeneratedExtension) this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                    if (extension3 != null) {
                        int position4 = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) extension3.getMessageDefaultInstance().getClass()), data, position3, limit, registers);
                        extensions.setField(extension3.descriptor, registers.object1);
                        position2 = position4;
                        extension = extension3;
                    } else {
                        position2 = ArrayDecoders.decodeUnknownField(startTag, data, position3, limit, unknownFields, registers);
                        extension = extension3;
                    }
                } else {
                    position2 = ArrayDecoders.skipField(startTag, data, position3, limit, registers);
                    extension = extension2;
                }
            } else {
                int typeId = 0;
                ByteString rawBytes = null;
                while (true) {
                    if (position3 < limit) {
                        int position5 = ArrayDecoders.decodeVarint32(data, position3, registers);
                        int tag = registers.int1;
                        int number = WireFormat.getTagFieldNumber(tag);
                        int wireType = WireFormat.getTagWireType(tag);
                        switch (number) {
                            case 2:
                                if (wireType == 0) {
                                    position3 = ArrayDecoders.decodeVarint32(data, position5, registers);
                                    typeId = registers.int1;
                                    extension2 = (GeneratedMessageLite.GeneratedExtension) this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, typeId);
                                    startTag = startTag;
                                } else if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                    position2 = position5;
                                } else {
                                    position3 = ArrayDecoders.skipField(tag, data, position5, limit, registers);
                                    startTag = startTag;
                                }
                                break;
                            case 3:
                                if (extension2 != null) {
                                    position3 = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) extension2.getMessageDefaultInstance().getClass()), data, position5, limit, registers);
                                    extensions.setField(extension2.descriptor, registers.object1);
                                } else if (wireType == 2) {
                                    position3 = ArrayDecoders.decodeBytes(data, position5, registers);
                                    rawBytes = (ByteString) registers.object1;
                                } else if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                    position2 = position5;
                                } else {
                                    position3 = ArrayDecoders.skipField(tag, data, position5, limit, registers);
                                    startTag = startTag;
                                }
                                break;
                            default:
                                if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG) {
                                    position2 = position5;
                                } else {
                                    position3 = ArrayDecoders.skipField(tag, data, position5, limit, registers);
                                    startTag = startTag;
                                }
                                break;
                        }
                    } else {
                        position2 = position3;
                    }
                }
                if (rawBytes != null) {
                    unknownFields.storeField(WireFormat.makeTag(typeId, 2), rawBytes);
                }
                extension = extension2;
            }
        }
        if (position2 != limit) {
            throw InvalidProtocolBufferException.parseFailure();
        }
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws Throwable {
        mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws Throwable {
        UnknownFieldSchema<UT, UB> unknownFieldSchema2;
        Throwable th;
        UB unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
        Object mutableExtensions = extensionSchema.getMutableExtensions(message);
        while (true) {
            try {
                int number = reader.getFieldNumber();
                if (number != Integer.MAX_VALUE) {
                    unknownFieldSchema2 = unknownFieldSchema;
                    ExtensionSchema<ET> extensionSchema2 = extensionSchema;
                    Reader reader2 = reader;
                    ExtensionRegistryLite extensionRegistry2 = extensionRegistry;
                    try {
                        if (parseMessageSetItemOrUnknownField(reader2, extensionRegistry2, extensionSchema2, mutableExtensions, unknownFieldSchema2, unknownFields)) {
                            reader = reader2;
                            extensionRegistry = extensionRegistry2;
                            extensionSchema = extensionSchema2;
                            unknownFieldSchema = unknownFieldSchema2;
                        } else {
                            unknownFieldSchema2.setBuilderToMessage(message, unknownFields);
                            return;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        unknownFieldSchema2.setBuilderToMessage(message, unknownFields);
                        throw th;
                    }
                } else {
                    unknownFieldSchema.setBuilderToMessage(message, unknownFields);
                    return;
                }
            } catch (Throwable th3) {
                unknownFieldSchema2 = unknownFieldSchema;
                th = th3;
            }
        }
    }

    @Override // com.google.protobuf.Schema
    public void makeImmutable(T message) {
        this.unknownFieldSchema.makeImmutable(message);
        this.extensionSchema.makeImmutable(message);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> boolean parseMessageSetItemOrUnknownField(Reader reader, ExtensionRegistryLite extensionRegistry, ExtensionSchema<ET> extensionSchema, FieldSet<ET> fieldSet, UnknownFieldSchema<UT, UB> unknownFieldSchema, UB unknownFields) throws IOException {
        int startTag = reader.getTag();
        if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
            if (WireFormat.getTagWireType(startTag) == 2) {
                Object extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, WireFormat.getTagFieldNumber(startTag));
                if (extension != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, fieldSet);
                    return true;
                }
                return unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader, 0);
            }
            return reader.skipField();
        }
        int typeId = 0;
        ByteString rawBytes = null;
        Object extension2 = null;
        while (number != Integer.MAX_VALUE) {
            int tag = reader.getTag();
            if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                typeId = reader.readUInt32();
                extension2 = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, typeId);
            } else if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                if (extension2 != null) {
                    extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension2, extensionRegistry, fieldSet);
                } else {
                    rawBytes = reader.readBytes();
                }
            } else if (!reader.skipField()) {
                break;
            }
        }
        int number = reader.getTag();
        if (number != WireFormat.MESSAGE_SET_ITEM_END_TAG) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
        if (rawBytes != null) {
            if (extension2 != null) {
                extensionSchema.parseMessageSetItem(rawBytes, extension2, extensionRegistry, fieldSet);
            } else {
                unknownFieldSchema.addLengthDelimited(unknownFields, typeId, rawBytes);
            }
        }
        return true;
    }

    @Override // com.google.protobuf.Schema
    public final boolean isInitialized(T message) {
        return this.extensionSchema.getExtensions(message).isInitialized();
    }

    @Override // com.google.protobuf.Schema
    public int getSerializedSize(T message) {
        int size = 0 + getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
        if (this.hasExtensions) {
            return size + this.extensionSchema.getExtensions(message).getMessageSetSerializedSize();
        }
        return size;
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSizeAsMessageSet(unknowns);
    }
}
