package com.google.protobuf;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.misc.Unsafe;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
@CheckReturnValue
final class MessageSchema<T> implements Schema<T> {
    private static final int CHECK_INITIALIZED_BIT = 1024;
    private static final int ENFORCE_UTF8_MASK = 536870912;
    private static final int FIELD_TYPE_MASK = 267386880;
    private static final int HAS_HAS_BIT = 4096;
    private static final int INTS_PER_FIELD = 3;
    private static final int LEGACY_ENUM_IS_CLOSED_BIT = 2048;
    private static final int LEGACY_ENUM_IS_CLOSED_MASK = Integer.MIN_VALUE;
    private static final int NO_PRESENCE_SENTINEL = 1048575;
    private static final int OFFSET_BITS = 20;
    private static final int OFFSET_MASK = 1048575;
    static final int ONEOF_TYPE_OFFSET = 51;
    private static final int REQUIRED_BIT = 256;
    private static final int REQUIRED_MASK = 268435456;
    private static final int UTF8_CHECK_BIT = 512;
    private final int[] buffer;
    private final int checkInitializedCount;
    private final MessageLite defaultInstance;
    private final ExtensionSchema<?> extensionSchema;
    private final boolean hasExtensions;
    private final int[] intArray;
    private final ListFieldSchema listFieldSchema;
    private final boolean lite;
    private final MapFieldSchema mapFieldSchema;
    private final int maxFieldNumber;
    private final int minFieldNumber;
    private final NewInstanceSchema newInstanceSchema;
    private final Object[] objects;
    private final int repeatedFieldOffsetStart;
    private final ProtoSyntax syntax;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;
    private final boolean useCachedSizeField;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    private MessageSchema(int[] buffer, Object[] objects, int minFieldNumber, int maxFieldNumber, MessageLite defaultInstance, ProtoSyntax syntax, boolean useCachedSizeField, int[] intArray, int checkInitialized, int mapFieldPositions, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        this.buffer = buffer;
        this.objects = objects;
        this.minFieldNumber = minFieldNumber;
        this.maxFieldNumber = maxFieldNumber;
        this.lite = defaultInstance instanceof GeneratedMessageLite;
        this.syntax = syntax;
        this.hasExtensions = extensionSchema != null && extensionSchema.hasExtensions(defaultInstance);
        this.useCachedSizeField = useCachedSizeField;
        this.intArray = intArray;
        this.checkInitializedCount = checkInitialized;
        this.repeatedFieldOffsetStart = mapFieldPositions;
        this.newInstanceSchema = newInstanceSchema;
        this.listFieldSchema = listFieldSchema;
        this.unknownFieldSchema = unknownFieldSchema;
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
        this.mapFieldSchema = mapFieldSchema;
    }

    static <T> MessageSchema<T> newSchema(Class<T> messageClass, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        if (messageInfo instanceof RawMessageInfo) {
            return newSchemaForRawMessageInfo((RawMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
        }
        return newSchemaForMessageInfo((StructuralMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    static <T> MessageSchema<T> newSchemaForRawMessageInfo(RawMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int objectsPosition;
        int numEntries;
        int mapFieldCount;
        int checkInitialized;
        int mapFieldCount2;
        int repeatedFieldCount;
        int hasBitsCount;
        int[] intArray;
        int i;
        int next;
        int i2;
        int next2;
        int i3;
        int next3;
        int i4;
        int next4;
        int i5;
        int next5;
        int i6;
        int next6;
        int i7;
        int next7;
        int i8;
        int next8;
        int length;
        int i9;
        int fieldCount;
        int unusedFlags;
        int fieldNumber;
        String info;
        int presenceFieldOffset;
        int i10;
        int presenceMaskShift;
        int presenceFieldOffset2;
        int next9;
        int next10;
        int fieldOffset;
        int result;
        int next11;
        Field hasBitsField;
        int i11;
        int next12;
        int i12;
        Field oneofField;
        Field oneofCaseField;
        int i13;
        int next13;
        int i14;
        int next14;
        int i15;
        int next15;
        int i16;
        int next16;
        int i17;
        int next17;
        String info2 = messageInfo.getStringInfo();
        int length2 = info2.length();
        int i18 = 0 + 1;
        int next18 = info2.charAt(0);
        int i19 = 55296;
        if (next18 >= 55296) {
            int result2 = next18 & 8191;
            int shift = 13;
            while (true) {
                i17 = i18 + 1;
                next17 = info2.charAt(i18);
                if (next17 < 55296) {
                    break;
                }
                result2 |= (next17 & 8191) << shift;
                shift += 13;
                i18 = i17;
            }
            next18 = result2 | (next17 << shift);
            i18 = i17;
        }
        int unusedFlags2 = next18;
        int i20 = i18 + 1;
        int next19 = info2.charAt(i18);
        if (next19 >= 55296) {
            int result3 = next19 & 8191;
            int shift2 = 13;
            while (true) {
                i16 = i20 + 1;
                next16 = info2.charAt(i20);
                if (next16 < 55296) {
                    break;
                }
                result3 |= (next16 & 8191) << shift2;
                shift2 += 13;
                i20 = i16;
            }
            next19 = result3 | (next16 << shift2);
            i20 = i16;
        }
        int fieldCount2 = next19;
        if (fieldCount2 == 0) {
            hasBitsCount = 0;
            int[] intArray2 = EMPTY_INT_ARRAY;
            objectsPosition = 0;
            numEntries = 0;
            mapFieldCount = 0;
            checkInitialized = 0;
            mapFieldCount2 = 0;
            repeatedFieldCount = 0;
            intArray = intArray2;
        } else {
            int i21 = i20 + 1;
            int next20 = info2.charAt(i20);
            if (next20 >= 55296) {
                int result4 = next20 & 8191;
                int shift3 = 13;
                while (true) {
                    i8 = i21 + 1;
                    next8 = info2.charAt(i21);
                    if (next8 < 55296) {
                        break;
                    }
                    result4 |= (next8 & 8191) << shift3;
                    shift3 += 13;
                    i21 = i8;
                }
                next20 = result4 | (next8 << shift3);
                i21 = i8;
            }
            int result5 = next20;
            int i22 = i21 + 1;
            int next21 = info2.charAt(i21);
            if (next21 >= 55296) {
                int result6 = next21 & 8191;
                int shift4 = 13;
                while (true) {
                    i7 = i22 + 1;
                    next7 = info2.charAt(i22);
                    if (next7 < 55296) {
                        break;
                    }
                    result6 |= (next7 & 8191) << shift4;
                    shift4 += 13;
                    i22 = i7;
                }
                next21 = result6 | (next7 << shift4);
                i22 = i7;
            }
            int result7 = next21;
            int i23 = i22 + 1;
            int next22 = info2.charAt(i22);
            if (next22 >= 55296) {
                int result8 = next22 & 8191;
                int shift5 = 13;
                while (true) {
                    i6 = i23 + 1;
                    next6 = info2.charAt(i23);
                    if (next6 < 55296) {
                        break;
                    }
                    result8 |= (next6 & 8191) << shift5;
                    shift5 += 13;
                    i23 = i6;
                }
                next22 = result8 | (next6 << shift5);
                i23 = i6;
            }
            int result9 = next22;
            int i24 = i23 + 1;
            int next23 = info2.charAt(i23);
            if (next23 >= 55296) {
                int result10 = next23 & 8191;
                int shift6 = 13;
                while (true) {
                    i5 = i24 + 1;
                    next5 = info2.charAt(i24);
                    if (next5 < 55296) {
                        break;
                    }
                    result10 |= (next5 & 8191) << shift6;
                    shift6 += 13;
                    i24 = i5;
                }
                next23 = result10 | (next5 << shift6);
                i24 = i5;
            }
            int result11 = next23;
            int i25 = i24 + 1;
            int next24 = info2.charAt(i24);
            if (next24 >= 55296) {
                int result12 = next24 & 8191;
                int shift7 = 13;
                while (true) {
                    i4 = i25 + 1;
                    next4 = info2.charAt(i25);
                    if (next4 < 55296) {
                        break;
                    }
                    result12 |= (next4 & 8191) << shift7;
                    shift7 += 13;
                    i25 = i4;
                }
                next24 = result12 | (next4 << shift7);
                i25 = i4;
            }
            int result13 = next24;
            int i26 = i25 + 1;
            int next25 = info2.charAt(i25);
            if (next25 >= 55296) {
                int result14 = next25 & 8191;
                int shift8 = 13;
                while (true) {
                    i3 = i26 + 1;
                    next3 = info2.charAt(i26);
                    if (next3 < 55296) {
                        break;
                    }
                    result14 |= (next3 & 8191) << shift8;
                    shift8 += 13;
                    i26 = i3;
                }
                next25 = result14 | (next3 << shift8);
                i26 = i3;
            }
            int result15 = next25;
            int i27 = i26 + 1;
            int next26 = info2.charAt(i26);
            if (next26 >= 55296) {
                int result16 = next26 & 8191;
                int shift9 = 13;
                while (true) {
                    i2 = i27 + 1;
                    next2 = info2.charAt(i27);
                    if (next2 < 55296) {
                        break;
                    }
                    result16 |= (next2 & 8191) << shift9;
                    shift9 += 13;
                    i27 = i2;
                }
                next26 = result16 | (next2 << shift9);
                i27 = i2;
            }
            int result17 = next26;
            int i28 = i27 + 1;
            int next27 = info2.charAt(i27);
            if (next27 >= 55296) {
                int result18 = next27 & 8191;
                int shift10 = 13;
                while (true) {
                    i = i28 + 1;
                    next = info2.charAt(i28);
                    if (next < 55296) {
                        break;
                    }
                    result18 |= (next & 8191) << shift10;
                    shift10 += 13;
                    i28 = i;
                }
                next27 = result18 | (next << shift10);
                i28 = i;
            }
            int result19 = next27;
            int[] intArray3 = new int[result19 + result15 + result17];
            objectsPosition = (result5 * 2) + result7;
            numEntries = result13;
            mapFieldCount = result15;
            checkInitialized = result19;
            mapFieldCount2 = result9;
            repeatedFieldCount = result11;
            hasBitsCount = result5;
            i20 = i28;
            intArray = intArray3;
        }
        Unsafe unsafe = UNSAFE;
        Object[] messageInfoObjects = messageInfo.getObjects();
        Class<?> messageClass = messageInfo.getDefaultInstance().getClass();
        int[] buffer = new int[numEntries * 3];
        Object[] objects = new Object[numEntries * 2];
        int mapFieldIndex = checkInitialized;
        int repeatedFieldIndex = checkInitialized + mapFieldCount;
        int checkInitializedPosition = 0;
        int objectsPosition2 = objectsPosition;
        int mapFieldIndex2 = mapFieldIndex;
        int repeatedFieldIndex2 = repeatedFieldIndex;
        int bufferIndex = 0;
        while (i20 < length2) {
            int i29 = i20 + 1;
            int next28 = info2.charAt(i20);
            if (next28 >= i19) {
                int result20 = next28 & 8191;
                int shift11 = 13;
                while (true) {
                    i15 = i29 + 1;
                    next15 = info2.charAt(i29);
                    if (next15 < i19) {
                        break;
                    }
                    result20 |= (next15 & 8191) << shift11;
                    shift11 += 13;
                    i29 = i15;
                }
                next28 = result20 | (next15 << shift11);
                i29 = i15;
            }
            int result21 = next28;
            int i30 = i29 + 1;
            int next29 = info2.charAt(i29);
            if (next29 < i19) {
                length = length2;
                i9 = i30;
            } else {
                int result22 = next29 & 8191;
                int i31 = i30;
                int i32 = 13;
                while (true) {
                    i14 = i31 + 1;
                    next14 = info2.charAt(i31);
                    length = length2;
                    if (next14 < 55296) {
                        break;
                    }
                    result22 |= (next14 & 8191) << i32;
                    i32 += 13;
                    i31 = i14;
                    length2 = length;
                }
                next29 = result22 | (next14 << i32);
                i9 = i14;
            }
            int fieldTypeWithExtraBits = next29;
            int fieldType = fieldTypeWithExtraBits & 255;
            int next30 = next29;
            if ((fieldTypeWithExtraBits & CHECK_INITIALIZED_BIT) != 0) {
                intArray[checkInitializedPosition] = bufferIndex;
                checkInitializedPosition++;
            }
            int i33 = 0;
            if (fieldType >= ONEOF_TYPE_OFFSET) {
                int i34 = i9 + 1;
                int next31 = info2.charAt(i9);
                if (next31 < 55296) {
                    fieldCount = fieldCount2;
                    i12 = i34;
                } else {
                    int result23 = next31 & 8191;
                    int result24 = i34;
                    int i35 = 13;
                    while (true) {
                        i13 = result24 + 1;
                        next13 = info2.charAt(result24);
                        fieldCount = fieldCount2;
                        if (next13 < 55296) {
                            break;
                        }
                        result23 |= (next13 & 8191) << i35;
                        i35 += 13;
                        result24 = i13;
                        fieldCount2 = fieldCount;
                    }
                    next31 = result23 | (next13 << i35);
                    i12 = i13;
                }
                int oneofIndex = next31;
                next9 = next31;
                int next32 = fieldType - 51;
                int i36 = i12;
                if (next32 == 9 || next32 == 17) {
                    int oneofFieldType = bufferIndex / 3;
                    objects[(oneofFieldType * 2) + 1] = messageInfoObjects[objectsPosition2];
                    objectsPosition2++;
                } else if (next32 == 12 && (messageInfo.getSyntax().equals(ProtoSyntax.PROTO2) || (fieldTypeWithExtraBits & LEGACY_ENUM_IS_CLOSED_BIT) != 0)) {
                    objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition2];
                    objectsPosition2++;
                }
                int index = oneofIndex * 2;
                Object o = messageInfoObjects[index];
                if (o instanceof Field) {
                    oneofField = (Field) o;
                } else {
                    oneofField = reflectField(messageClass, (String) o);
                    messageInfoObjects[index] = oneofField;
                }
                int fieldOffset2 = (int) unsafe.objectFieldOffset(oneofField);
                int index2 = index + 1;
                Object o2 = messageInfoObjects[index2];
                if (o2 instanceof Field) {
                    oneofCaseField = (Field) o2;
                } else {
                    oneofCaseField = reflectField(messageClass, (String) o2);
                    messageInfoObjects[index2] = oneofCaseField;
                }
                unusedFlags = unusedFlags2;
                fieldNumber = result21;
                fieldOffset = (int) unsafe.objectFieldOffset(oneofCaseField);
                presenceMaskShift = 0;
                info = info2;
                i20 = i36;
                next10 = fieldOffset2;
            } else {
                fieldCount = fieldCount2;
                unusedFlags = unusedFlags2;
                fieldNumber = result21;
                int objectsPosition3 = objectsPosition2 + 1;
                Field field = reflectField(messageClass, (String) messageInfoObjects[objectsPosition2]);
                if (fieldType == 9 || fieldType == 17) {
                    objects[((bufferIndex / 3) * 2) + 1] = field.getType();
                } else if (fieldType == 27 || fieldType == 49) {
                    objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition3];
                    objectsPosition3++;
                } else if (fieldType == 12 || fieldType == 30 || fieldType == 44) {
                    if (messageInfo.getSyntax() == ProtoSyntax.PROTO2 || (fieldTypeWithExtraBits & LEGACY_ENUM_IS_CLOSED_BIT) != 0) {
                        objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition3];
                        objectsPosition3++;
                    }
                } else if (fieldType == 50) {
                    int mapFieldIndex3 = mapFieldIndex2 + 1;
                    intArray[mapFieldIndex2] = bufferIndex;
                    int objectsPosition4 = objectsPosition3 + 1;
                    objects[(bufferIndex / 3) * 2] = messageInfoObjects[objectsPosition3];
                    if ((fieldTypeWithExtraBits & LEGACY_ENUM_IS_CLOSED_BIT) == 0) {
                        mapFieldIndex2 = mapFieldIndex3;
                        objectsPosition3 = objectsPosition4;
                    } else {
                        objects[((bufferIndex / 3) * 2) + 1] = messageInfoObjects[objectsPosition4];
                        mapFieldIndex2 = mapFieldIndex3;
                        objectsPosition3 = objectsPosition4 + 1;
                    }
                }
                int fieldOffset3 = (int) unsafe.objectFieldOffset(field);
                boolean hasHasBit = (fieldTypeWithExtraBits & 4096) != 0;
                if (!hasHasBit || fieldType > 17) {
                    info = info2;
                    presenceFieldOffset = 1048575;
                    i10 = i9;
                    presenceMaskShift = 0;
                    presenceFieldOffset2 = next30;
                } else {
                    int i37 = i9 + 1;
                    int next33 = info2.charAt(i9);
                    if (next33 < 55296) {
                        info = info2;
                        result = next33;
                        next11 = i37;
                    } else {
                        int result25 = next33 & 8191;
                        int shift12 = 13;
                        while (true) {
                            i11 = i37 + 1;
                            next12 = info2.charAt(i37);
                            info = info2;
                            if (next12 < 55296) {
                                break;
                            }
                            result25 |= (next12 & 8191) << shift12;
                            shift12 += 13;
                            i37 = i11;
                            info2 = info;
                        }
                        result = result25 | (next12 << shift12);
                        next11 = i11;
                    }
                    int i38 = result;
                    int index3 = (hasBitsCount * 2) + (i38 / 32);
                    Object o3 = messageInfoObjects[index3];
                    i10 = next11;
                    if (o3 instanceof Field) {
                        hasBitsField = (Field) o3;
                    } else {
                        hasBitsField = reflectField(messageClass, (String) o3);
                        messageInfoObjects[index3] = hasBitsField;
                    }
                    int next34 = result;
                    int presenceFieldOffset3 = (int) unsafe.objectFieldOffset(hasBitsField);
                    int presenceMaskShift2 = i38 % 32;
                    presenceFieldOffset = presenceFieldOffset3;
                    presenceMaskShift = presenceMaskShift2;
                    presenceFieldOffset2 = next34;
                }
                if (fieldType >= 18 && fieldType <= 49) {
                    intArray[repeatedFieldIndex2] = fieldOffset3;
                    next9 = presenceFieldOffset2;
                    repeatedFieldIndex2++;
                    next10 = fieldOffset3;
                    objectsPosition2 = objectsPosition3;
                    i20 = i10;
                    fieldOffset = presenceFieldOffset;
                } else {
                    next9 = presenceFieldOffset2;
                    next10 = fieldOffset3;
                    objectsPosition2 = objectsPosition3;
                    i20 = i10;
                    fieldOffset = presenceFieldOffset;
                }
            }
            int presenceFieldOffset4 = bufferIndex + 1;
            buffer[bufferIndex] = fieldNumber;
            int bufferIndex2 = presenceFieldOffset4 + 1;
            int i39 = ((fieldTypeWithExtraBits & UTF8_CHECK_BIT) != 0 ? ENFORCE_UTF8_MASK : 0) | ((fieldTypeWithExtraBits & REQUIRED_BIT) != 0 ? REQUIRED_MASK : 0);
            if ((fieldTypeWithExtraBits & LEGACY_ENUM_IS_CLOSED_BIT) != 0) {
                i33 = LEGACY_ENUM_IS_CLOSED_MASK;
            }
            buffer[presenceFieldOffset4] = i39 | i33 | (fieldType << OFFSET_BITS) | next10;
            bufferIndex = bufferIndex2 + 1;
            buffer[bufferIndex2] = (presenceMaskShift << OFFSET_BITS) | fieldOffset;
            length2 = length;
            info2 = info;
            unusedFlags2 = unusedFlags;
            fieldCount2 = fieldCount;
            i19 = 55296;
        }
        return new MessageSchema<>(buffer, objects, mapFieldCount2, repeatedFieldCount, messageInfo.getDefaultInstance(), messageInfo.getSyntax(), false, intArray, checkInitialized, checkInitialized + mapFieldCount, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static Field reflectField(Class<?> messageClass, String fieldName) {
        try {
            return messageClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Field[] fields = messageClass.getDeclaredFields();
            for (Field field : fields) {
                if (fieldName.equals(field.getName())) {
                    return field;
                }
            }
            throw new RuntimeException("Field " + fieldName + " for " + messageClass.getName() + " not found. Known fields are " + Arrays.toString(fields));
        }
    }

    static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int minFieldNumber;
        int maxFieldNumber;
        int[] mapFieldPositions;
        int[] repeatedFieldOffsets;
        FieldInfo[] fis = messageInfo.getFields();
        if (fis.length == 0) {
            minFieldNumber = 0;
            maxFieldNumber = 0;
        } else {
            int minFieldNumber2 = fis[0].getFieldNumber();
            minFieldNumber = minFieldNumber2;
            maxFieldNumber = fis[fis.length - 1].getFieldNumber();
        }
        int minFieldNumber3 = fis.length;
        int[] buffer = new int[minFieldNumber3 * 3];
        Object[] objects = new Object[minFieldNumber3 * 2];
        int mapFieldCount = 0;
        int repeatedFieldCount = 0;
        for (FieldInfo fi : fis) {
            if (fi.getType() == FieldType.MAP) {
                mapFieldCount++;
            } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
                repeatedFieldCount++;
            }
        }
        int[] mapFieldPositions2 = mapFieldCount > 0 ? new int[mapFieldCount] : null;
        int[] repeatedFieldOffsets2 = repeatedFieldCount > 0 ? new int[repeatedFieldCount] : null;
        int mapFieldCount2 = 0;
        int[] checkInitialized = messageInfo.getCheckInitialized();
        if (checkInitialized == null) {
            checkInitialized = EMPTY_INT_ARRAY;
        }
        int repeatedFieldCount2 = 0;
        int checkInitializedIndex = 0;
        int checkInitializedIndex2 = 0;
        int fieldIndex = 0;
        while (checkInitializedIndex2 < fis.length) {
            FieldInfo fi2 = fis[checkInitializedIndex2];
            int fieldNumber = fi2.getFieldNumber();
            storeFieldData(fi2, buffer, fieldIndex, objects);
            if (checkInitializedIndex < checkInitialized.length && checkInitialized[checkInitializedIndex] == fieldNumber) {
                checkInitialized[checkInitializedIndex] = fieldIndex;
                checkInitializedIndex++;
            }
            FieldInfo[] fis2 = fis;
            if (fi2.getType() == FieldType.MAP) {
                mapFieldPositions2[mapFieldCount2] = fieldIndex;
                mapFieldCount2++;
            } else if (fi2.getType().id() >= 18 && fi2.getType().id() <= 49) {
                repeatedFieldOffsets2[repeatedFieldCount2] = (int) UnsafeUtil.objectFieldOffset(fi2.getField());
                repeatedFieldCount2++;
            }
            checkInitializedIndex2++;
            fieldIndex += 3;
            checkInitialized = checkInitialized;
            fis = fis2;
        }
        int[] checkInitialized2 = checkInitialized;
        if (mapFieldPositions2 != null) {
            mapFieldPositions = mapFieldPositions2;
        } else {
            mapFieldPositions = EMPTY_INT_ARRAY;
        }
        if (repeatedFieldOffsets2 != null) {
            repeatedFieldOffsets = repeatedFieldOffsets2;
        } else {
            repeatedFieldOffsets = EMPTY_INT_ARRAY;
        }
        int[] combined = new int[checkInitialized2.length + mapFieldPositions.length + repeatedFieldOffsets.length];
        System.arraycopy(checkInitialized2, 0, combined, 0, checkInitialized2.length);
        System.arraycopy(mapFieldPositions, 0, combined, checkInitialized2.length, mapFieldPositions.length);
        System.arraycopy(repeatedFieldOffsets, 0, combined, checkInitialized2.length + mapFieldPositions.length, repeatedFieldOffsets.length);
        MessageLite defaultInstance = messageInfo.getDefaultInstance();
        ProtoSyntax syntax = messageInfo.getSyntax();
        int length = checkInitialized2.length;
        int numEntries = checkInitialized2.length;
        int fieldIndex2 = numEntries + mapFieldPositions.length;
        return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, defaultInstance, syntax, true, combined, length, fieldIndex2, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static void storeFieldData(FieldInfo fi, int[] buffer, int bufferIndex, Object[] objects) {
        int fieldOffset;
        int typeId;
        int typeId2;
        int presenceFieldOffset;
        int presenceFieldOffset2;
        OneofInfo oneof = fi.getOneof();
        if (oneof != null) {
            typeId = fi.getType().id() + ONEOF_TYPE_OFFSET;
            fieldOffset = (int) UnsafeUtil.objectFieldOffset(oneof.getValueField());
            typeId2 = (int) UnsafeUtil.objectFieldOffset(oneof.getCaseField());
            presenceFieldOffset = 0;
        } else {
            FieldType type = fi.getType();
            fieldOffset = (int) UnsafeUtil.objectFieldOffset(fi.getField());
            int typeId3 = type.id();
            if (!type.isList() && !type.isMap()) {
                Field presenceField = fi.getPresenceField();
                if (presenceField == null) {
                    presenceFieldOffset2 = 1048575;
                } else {
                    presenceFieldOffset2 = (int) UnsafeUtil.objectFieldOffset(presenceField);
                }
                presenceFieldOffset = Integer.numberOfTrailingZeros(fi.getPresenceMask());
                typeId = typeId3;
                typeId2 = presenceFieldOffset2;
            } else if (fi.getCachedSizeField() == null) {
                typeId = typeId3;
                typeId2 = 0;
                presenceFieldOffset = 0;
            } else {
                int presenceFieldOffset3 = (int) UnsafeUtil.objectFieldOffset(fi.getCachedSizeField());
                typeId = typeId3;
                typeId2 = presenceFieldOffset3;
                presenceFieldOffset = 0;
            }
        }
        buffer[bufferIndex] = fi.getFieldNumber();
        buffer[bufferIndex + 1] = (fi.isEnforceUtf8() ? ENFORCE_UTF8_MASK : 0) | (fi.isRequired() ? REQUIRED_MASK : 0) | (typeId << OFFSET_BITS) | fieldOffset;
        buffer[bufferIndex + 2] = (presenceFieldOffset << OFFSET_BITS) | typeId2;
        Object messageFieldClass = fi.getMessageFieldClass();
        if (fi.getMapDefaultEntry() != null) {
            objects[(bufferIndex / 3) * 2] = fi.getMapDefaultEntry();
            if (messageFieldClass != null) {
                objects[((bufferIndex / 3) * 2) + 1] = messageFieldClass;
                return;
            } else {
                if (fi.getEnumVerifier() != null) {
                    objects[((bufferIndex / 3) * 2) + 1] = fi.getEnumVerifier();
                    return;
                }
                return;
            }
        }
        if (messageFieldClass != null) {
            objects[((bufferIndex / 3) * 2) + 1] = messageFieldClass;
        } else if (fi.getEnumVerifier() != null) {
            objects[((bufferIndex / 3) * 2) + 1] = fi.getEnumVerifier();
        }
    }

    @Override // com.google.protobuf.Schema
    public T newInstance() {
        return (T) this.newInstanceSchema.newInstance(this.defaultInstance);
    }

    @Override // com.google.protobuf.Schema
    public boolean equals(T message, T other) {
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            if (!equals(message, other, pos)) {
                return false;
            }
        }
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

    private boolean equals(T message, T other, int pos) {
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        switch (type(typeAndOffset)) {
            case 0:
                return arePresentForEquals(message, other, pos) && Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)) == Double.doubleToLongBits(UnsafeUtil.getDouble(other, offset));
            case 1:
                return arePresentForEquals(message, other, pos) && Float.floatToIntBits(UnsafeUtil.getFloat(message, offset)) == Float.floatToIntBits(UnsafeUtil.getFloat(other, offset));
            case 2:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 3:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 4:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 6:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 7:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getBoolean(message, offset) == UnsafeUtil.getBoolean(other, offset);
            case 8:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 9:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 10:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 11:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 12:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 13:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 14:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 15:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            case 16:
                return arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            case 17:
                return arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 18:
            case 19:
            case OFFSET_BITS /* 20 */:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case 50:
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            case ONEOF_TYPE_OFFSET /* 51 */:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
                return isOneofCaseEqual(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            default:
                return true;
        }
    }

    @Override // com.google.protobuf.Schema
    public int hashCode(T message) {
        int hashCode = 0;
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = typeAndOffsetAt(pos);
            int entryNumber = numberAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 0:
                    hashCode = (hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)));
                    break;
                case 1:
                    hashCode = (hashCode * 53) + Float.floatToIntBits(UnsafeUtil.getFloat(message, offset));
                    break;
                case 2:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 3:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 4:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 6:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 7:
                    hashCode = (hashCode * 53) + Internal.hashBoolean(UnsafeUtil.getBoolean(message, offset));
                    break;
                case 8:
                    int protoHash = hashCode * 53;
                    int hashCode2 = protoHash + ((String) UnsafeUtil.getObject(message, offset)).hashCode();
                    hashCode = hashCode2;
                    break;
                case 9:
                    int protoHash2 = 37;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    if (submessage != null) {
                        protoHash2 = submessage.hashCode();
                    }
                    hashCode = (hashCode * 53) + protoHash2;
                    break;
                case 10:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case 11:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 12:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 13:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 14:
                    hashCode = (hashCode * 53) + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    break;
                case 15:
                    hashCode = (hashCode * 53) + UnsafeUtil.getInt(message, offset);
                    break;
                case 16:
                    int protoHash3 = hashCode * 53;
                    int hashCode3 = protoHash3 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    hashCode = hashCode3;
                    break;
                case 17:
                    int protoHash4 = 37;
                    Object submessage2 = UnsafeUtil.getObject(message, offset);
                    if (submessage2 != null) {
                        protoHash4 = submessage2.hashCode();
                    }
                    hashCode = (hashCode * 53) + protoHash4;
                    break;
                case 18:
                case 19:
                case OFFSET_BITS /* 20 */:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case 50:
                    hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    break;
                case ONEOF_TYPE_OFFSET /* 51 */:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(Double.doubleToLongBits(oneofDoubleAt(message, offset)));
                    }
                    break;
                case 52:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Float.floatToIntBits(oneofFloatAt(message, offset));
                    }
                    break;
                case 53:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                    }
                    break;
                case 54:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                    }
                    break;
                case 55:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 56:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                    }
                    break;
                case 57:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 58:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashBoolean(oneofBooleanAt(message, offset));
                    }
                    break;
                case 59:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + ((String) UnsafeUtil.getObject(message, offset)).hashCode();
                    }
                    break;
                case 60:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    }
                    break;
                case 61:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    }
                    break;
                case 62:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 63:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 64:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 65:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                    }
                    break;
                case 66:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + oneofIntAt(message, offset);
                    }
                    break;
                case 67:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + Internal.hashLong(oneofLongAt(message, offset));
                    }
                    break;
                case 68:
                    if (isOneofPresent(message, entryNumber, pos)) {
                        hashCode = (hashCode * 53) + UnsafeUtil.getObject(message, offset).hashCode();
                    }
                    break;
            }
        }
        int pos2 = hashCode * 53;
        int hashCode4 = pos2 + this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            return (hashCode4 * 53) + this.extensionSchema.getExtensions(message).hashCode();
        }
        return hashCode4;
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T message, T other) {
        checkMutable(message);
        if (other == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.buffer.length; i += 3) {
            mergeSingleField(message, other, i);
        }
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    private void mergeSingleField(T message, T other, int pos) {
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        int number = numberAt(pos);
        switch (type(typeAndOffset)) {
            case 0:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putDouble(message, offset, UnsafeUtil.getDouble(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 1:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putFloat(message, offset, UnsafeUtil.getFloat(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 2:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 3:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 4:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 6:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 7:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putBoolean(message, offset, UnsafeUtil.getBoolean(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 8:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 9:
                mergeMessage(message, other, pos);
                break;
            case 10:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 11:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 12:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 13:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 14:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 15:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 16:
                if (isFieldPresent(other, pos)) {
                    UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                    setFieldPresent(message, pos);
                }
                break;
            case 17:
                mergeMessage(message, other, pos);
                break;
            case 18:
            case 19:
            case OFFSET_BITS /* 20 */:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                this.listFieldSchema.mergeListsAt(message, other, offset);
                break;
            case 50:
                SchemaUtil.mergeMap(this.mapFieldSchema, message, other, offset);
                break;
            case ONEOF_TYPE_OFFSET /* 51 */:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                if (isOneofPresent(other, number, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setOneofPresent(message, number, pos);
                }
                break;
            case 60:
                mergeOneofMessage(message, other, pos);
                break;
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
                if (isOneofPresent(other, number, pos)) {
                    UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                    setOneofPresent(message, number, pos);
                }
                break;
            case 68:
                mergeOneofMessage(message, other, pos);
                break;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void mergeMessage(T targetParent, T sourceParent, int pos) {
        if (!isFieldPresent(sourceParent, pos)) {
            return;
        }
        int typeAndOffset = typeAndOffsetAt(pos);
        long offset = offset(typeAndOffset);
        Object source = UNSAFE.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema messageFieldSchema = getMessageFieldSchema(pos);
        if (!isFieldPresent(targetParent, pos)) {
            if (!isMutable(source)) {
                UNSAFE.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = messageFieldSchema.newInstance();
                messageFieldSchema.mergeFrom(copyOfSource, source);
                UNSAFE.putObject(targetParent, offset, copyOfSource);
            }
            setFieldPresent(targetParent, pos);
            return;
        }
        Object target = UNSAFE.getObject(targetParent, offset);
        if (!isMutable(target)) {
            Object newInstance = messageFieldSchema.newInstance();
            messageFieldSchema.mergeFrom(newInstance, target);
            UNSAFE.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        messageFieldSchema.mergeFrom(target, source);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void mergeOneofMessage(T targetParent, T sourceParent, int pos) {
        int number = numberAt(pos);
        if (!isOneofPresent(sourceParent, number, pos)) {
            return;
        }
        long offset = offset(typeAndOffsetAt(pos));
        Object source = UNSAFE.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema messageFieldSchema = getMessageFieldSchema(pos);
        if (!isOneofPresent(targetParent, number, pos)) {
            if (!isMutable(source)) {
                UNSAFE.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = messageFieldSchema.newInstance();
                messageFieldSchema.mergeFrom(copyOfSource, source);
                UNSAFE.putObject(targetParent, offset, copyOfSource);
            }
            setOneofPresent(targetParent, number, pos);
            return;
        }
        Object target = UNSAFE.getObject(targetParent, offset);
        if (!isMutable(target)) {
            Object newInstance = messageFieldSchema.newInstance();
            messageFieldSchema.mergeFrom(newInstance, target);
            UNSAFE.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        messageFieldSchema.mergeFrom(target, source);
    }

    @Override // com.google.protobuf.Schema
    public int getSerializedSize(T message) {
        int cachedSizeOffset;
        int size;
        int size2;
        MessageSchema<T> messageSchema = this;
        T t = message;
        Unsafe unsafe = UNSAFE;
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceField = 0;
        int size3 = 0;
        for (int size4 = 0; size4 < messageSchema.buffer.length; size4 += 3) {
            int typeAndOffset = messageSchema.typeAndOffsetAt(size4);
            int fieldType = type(typeAndOffset);
            int number = messageSchema.numberAt(size4);
            int presenceMask = 0;
            int presenceMaskAndOffset = messageSchema.buffer[size4 + 2];
            int presenceOrCachedSizeFieldOffset = presenceMaskAndOffset & 1048575;
            if (fieldType <= 17) {
                if (presenceOrCachedSizeFieldOffset != currentPresenceFieldOffset) {
                    currentPresenceFieldOffset = presenceOrCachedSizeFieldOffset;
                    currentPresenceField = currentPresenceFieldOffset == 1048575 ? 0 : unsafe.getInt(t, currentPresenceFieldOffset);
                }
                int presenceMask2 = presenceMaskAndOffset >>> OFFSET_BITS;
                presenceMask = 1 << presenceMask2;
            }
            int size5 = size3;
            long offset = offset(typeAndOffset);
            if (fieldType >= FieldType.DOUBLE_LIST_PACKED.id() && fieldType <= FieldType.SINT64_LIST_PACKED.id()) {
                cachedSizeOffset = presenceOrCachedSizeFieldOffset;
            } else {
                cachedSizeOffset = 0;
            }
            switch (fieldType) {
                case 0:
                    messageSchema = this;
                    t = message;
                    if (messageSchema.isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        size3 = size5 + CodedOutputStream.computeDoubleSize(number, 0.0d);
                    }
                    break;
                case 1:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeFloatSize(number, 0.0f);
                        messageSchema = this;
                    }
                    break;
                case 2:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeInt64Size(number, unsafe.getLong(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 3:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeUInt64Size(number, unsafe.getLong(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 4:
                    t = message;
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeInt32Size(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                    }
                    break;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    if (!isFieldPresent(message, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeFixed64Size(number, 0L);
                        messageSchema = this;
                    }
                    break;
                case 6:
                    if (!isFieldPresent(message, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeFixed32Size(number, 0);
                        messageSchema = this;
                    }
                    break;
                case 7:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeBoolSize(number, true);
                        messageSchema = this;
                    }
                    break;
                case 8:
                    if (!messageSchema.isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        Object value = unsafe.getObject(t, offset);
                        if (value instanceof ByteString) {
                            size = size5 + CodedOutputStream.computeBytesSize(number, (ByteString) value);
                        } else {
                            size = size5 + CodedOutputStream.computeStringSize(number, (String) value);
                        }
                        messageSchema = this;
                        size3 = size;
                    }
                    break;
                case 9:
                    messageSchema = this;
                    if (messageSchema.isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        size3 = size5 + SchemaUtil.computeSizeMessage(number, unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    }
                    break;
                case 10:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeBytesSize(number, (ByteString) unsafe.getObject(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 11:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeUInt32Size(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 12:
                    t = message;
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeEnumSize(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 13:
                    if (!isFieldPresent(message, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeSFixed32Size(number, 0);
                        messageSchema = this;
                    }
                    break;
                case 14:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                        t = message;
                    } else {
                        t = message;
                        size3 = size5 + CodedOutputStream.computeSFixed64Size(number, 0L);
                        messageSchema = this;
                    }
                    break;
                case 15:
                    if (!isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeSInt32Size(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 16:
                    if (!messageSchema.isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        messageSchema = this;
                    } else {
                        size3 = size5 + CodedOutputStream.computeSInt64Size(number, unsafe.getLong(t, offset));
                        messageSchema = this;
                    }
                    break;
                case 17:
                    if (messageSchema.isFieldPresent(t, size4, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                        size3 = size5 + CodedOutputStream.computeGroupSize(number, (MessageLite) unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    }
                    break;
                case 18:
                    size3 = size5 + SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 19:
                    size3 = size5 + SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case OFFSET_BITS /* 20 */:
                    size3 = size5 + SchemaUtil.computeSizeInt64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 21:
                    size3 = size5 + SchemaUtil.computeSizeUInt64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 22:
                    size3 = size5 + SchemaUtil.computeSizeInt32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 23:
                    size3 = size5 + SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 24:
                    size3 = size5 + SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 25:
                    size3 = size5 + SchemaUtil.computeSizeBoolList(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 26:
                    size3 = size5 + SchemaUtil.computeSizeStringList(number, (List) unsafe.getObject(t, offset));
                    continue;
                    break;
                case 27:
                    size3 = size5 + SchemaUtil.computeSizeMessageList(number, (List) unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    continue;
                    break;
                case 28:
                    size3 = size5 + SchemaUtil.computeSizeByteStringList(number, (List) unsafe.getObject(t, offset));
                    continue;
                    break;
                case 29:
                    size3 = size5 + SchemaUtil.computeSizeUInt32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 30:
                    size3 = size5 + SchemaUtil.computeSizeEnumList(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 31:
                    size3 = size5 + SchemaUtil.computeSizeFixed32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 32:
                    size3 = size5 + SchemaUtil.computeSizeFixed64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 33:
                    size3 = size5 + SchemaUtil.computeSizeSInt32List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 34:
                    size3 = size5 + SchemaUtil.computeSizeSInt64List(number, (List) unsafe.getObject(t, offset), false);
                    continue;
                    break;
                case 35:
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    }
                    break;
                case 36:
                    int fieldSize2 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize2 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize2);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize2) + fieldSize2;
                    }
                    break;
                case 37:
                    int fieldSize3 = SchemaUtil.computeSizeInt64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize3 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize3);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize3) + fieldSize3;
                    }
                    break;
                case 38:
                    int fieldSize4 = SchemaUtil.computeSizeUInt64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize4 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize4);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize4) + fieldSize4;
                    }
                    break;
                case 39:
                    int fieldSize5 = SchemaUtil.computeSizeInt32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize5 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize5);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize5) + fieldSize5;
                    }
                    break;
                case 40:
                    int fieldSize6 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize6 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize6);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize6) + fieldSize6;
                    }
                    break;
                case 41:
                    int fieldSize7 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize7 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize7);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize7) + fieldSize7;
                    }
                    break;
                case 42:
                    int fieldSize8 = SchemaUtil.computeSizeBoolListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize8 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize8);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize8) + fieldSize8;
                    }
                    break;
                case 43:
                    int fieldSize9 = SchemaUtil.computeSizeUInt32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize9 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize9);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize9) + fieldSize9;
                    }
                    break;
                case 44:
                    int fieldSize10 = SchemaUtil.computeSizeEnumListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize10 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize10);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize10) + fieldSize10;
                    }
                    break;
                case 45:
                    int fieldSize11 = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize11 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize11);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize11) + fieldSize11;
                    }
                    break;
                case 46:
                    int fieldSize12 = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize12 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize12);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize12) + fieldSize12;
                    }
                    break;
                case 47:
                    int fieldSize13 = SchemaUtil.computeSizeSInt32ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize13 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize13);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize13) + fieldSize13;
                    }
                    break;
                case 48:
                    int fieldSize14 = SchemaUtil.computeSizeSInt64ListNoTag((List) unsafe.getObject(t, offset));
                    if (fieldSize14 > 0) {
                        if (messageSchema.useCachedSizeField) {
                            unsafe.putInt(t, cachedSizeOffset, fieldSize14);
                        }
                        size3 = size5 + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize14) + fieldSize14;
                    }
                    break;
                case 49:
                    size3 = size5 + SchemaUtil.computeSizeGroupList(number, (List) unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    continue;
                    break;
                case 50:
                    size3 = size5 + messageSchema.mapFieldSchema.getSerializedSize(number, unsafe.getObject(t, offset), messageSchema.getMapFieldDefaultEntry(size4));
                    continue;
                    break;
                case ONEOF_TYPE_OFFSET /* 51 */:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeDoubleSize(number, 0.0d);
                    }
                    break;
                case 52:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeFloatSize(number, 0.0f);
                    }
                    break;
                case 53:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeInt64Size(number, oneofLongAt(t, offset));
                    }
                    break;
                case 54:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeUInt64Size(number, oneofLongAt(t, offset));
                    }
                    break;
                case 55:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeInt32Size(number, oneofIntAt(t, offset));
                    }
                    break;
                case 56:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeFixed64Size(number, 0L);
                    }
                    break;
                case 57:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeFixed32Size(number, 0);
                    }
                    break;
                case 58:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeBoolSize(number, true);
                    }
                    break;
                case 59:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        Object value2 = unsafe.getObject(t, offset);
                        if (value2 instanceof ByteString) {
                            size2 = size5 + CodedOutputStream.computeBytesSize(number, (ByteString) value2);
                        } else {
                            size2 = size5 + CodedOutputStream.computeStringSize(number, (String) value2);
                        }
                        size3 = size2;
                    }
                    break;
                case 60:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + SchemaUtil.computeSizeMessage(number, unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    }
                    break;
                case 61:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeBytesSize(number, (ByteString) unsafe.getObject(t, offset));
                    }
                    break;
                case 62:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeUInt32Size(number, oneofIntAt(t, offset));
                    }
                    break;
                case 63:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeEnumSize(number, oneofIntAt(t, offset));
                    }
                    break;
                case 64:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeSFixed32Size(number, 0);
                    }
                    break;
                case 65:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeSFixed64Size(number, 0L);
                    }
                    break;
                case 66:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeSInt32Size(number, oneofIntAt(t, offset));
                    }
                    break;
                case 67:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeSInt64Size(number, oneofLongAt(t, offset));
                    }
                    break;
                case 68:
                    if (messageSchema.isOneofPresent(t, number, size4)) {
                        size3 = size5 + CodedOutputStream.computeGroupSize(number, (MessageLite) unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(size4));
                    }
                    break;
            }
            size3 = size5;
        }
        int size6 = size3 + messageSchema.getUnknownFieldsSerializedSize(messageSchema.unknownFieldSchema, t);
        if (messageSchema.hasExtensions) {
            return size6 + messageSchema.extensionSchema.getExtensions(t).getSerializedSize();
        }
        return size6;
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSize(unknowns);
    }

    @Override // com.google.protobuf.Schema
    public void writeTo(T message, Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            writeFieldsInDescendingOrder(message, writer);
        } else {
            writeFieldsInAscendingOrder(message, writer);
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0025  */
    private void writeFieldsInAscendingOrder(T message, Writer writer) throws IOException {
        Iterator<? extends Map.Entry<?, ?>> extensionIterator;
        boolean z;
        int currentPresenceFieldOffset;
        Map.Entry<?, ?> entry;
        int currentPresenceFieldOffset2;
        MessageSchema<T> messageSchema = this;
        T t = message;
        Map.Entry entry2 = null;
        if (messageSchema.hasExtensions) {
            FieldSet<T> extensions = messageSchema.extensionSchema.getExtensions(t);
            if (!extensions.isEmpty()) {
                Iterator<? extends Map.Entry<?, ?>> extensionIterator2 = extensions.iterator();
                Map.Entry nextExtension = extensionIterator2.next();
                entry2 = nextExtension;
                extensionIterator = extensionIterator2;
            } else {
                extensionIterator = null;
            }
        } else {
            extensionIterator = null;
        }
        int currentPresenceFieldOffset3 = 1048575;
        int currentPresenceField = 0;
        int bufferLength = messageSchema.buffer.length;
        Unsafe unsafe = UNSAFE;
        int pos = 0;
        while (pos < bufferLength) {
            int typeAndOffset = messageSchema.typeAndOffsetAt(pos);
            int number = messageSchema.numberAt(pos);
            int fieldType = type(typeAndOffset);
            int presenceMask = 0;
            if (fieldType > 17) {
                Map.Entry<?, ?> entry3 = entry2;
                z = true;
                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                entry = entry3;
            } else {
                int presenceMaskAndOffset = messageSchema.buffer[pos + 2];
                z = true;
                int presenceFieldOffset = presenceMaskAndOffset & 1048575;
                if (presenceFieldOffset != currentPresenceFieldOffset3) {
                    int currentPresenceFieldOffset4 = presenceFieldOffset == 1048575 ? 0 : unsafe.getInt(t, presenceFieldOffset);
                    currentPresenceField = currentPresenceFieldOffset4;
                    currentPresenceFieldOffset3 = presenceFieldOffset;
                }
                presenceMask = 1 << (presenceMaskAndOffset >>> OFFSET_BITS);
                currentPresenceFieldOffset = currentPresenceFieldOffset3;
                entry = entry2;
            }
            while (entry != null && messageSchema.extensionSchema.extensionNumber(entry) <= number) {
                messageSchema.extensionSchema.serializeExtension(writer, entry);
                entry = extensionIterator.hasNext() ? (Map.Entry) extensionIterator.next() : null;
            }
            Iterator<? extends Map.Entry<?, ?>> extensionIterator3 = extensionIterator;
            int bufferLength2 = bufferLength;
            long offset = offset(typeAndOffset);
            switch (fieldType) {
                case 0:
                    int pos2 = pos;
                    int pos3 = presenceMask;
                    messageSchema = this;
                    boolean zIsFieldPresent = messageSchema.isFieldPresent(t, pos2, currentPresenceFieldOffset, currentPresenceField, pos3);
                    pos = pos2;
                    if (!zIsFieldPresent) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeDouble(number, doubleAt(t, offset));
                    }
                    break;
                case 1:
                    int pos4 = pos;
                    int pos5 = presenceMask;
                    if (!isFieldPresent(t, pos4, currentPresenceFieldOffset, currentPresenceField, pos5)) {
                        messageSchema = this;
                        pos = pos4;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeFloat(number, floatAt(t, offset));
                        messageSchema = this;
                        pos = pos4;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 2:
                    int pos6 = pos;
                    int pos7 = presenceMask;
                    if (!isFieldPresent(t, pos6, currentPresenceFieldOffset, currentPresenceField, pos7)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        pos = pos6;
                        messageSchema = this;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeInt64(number, unsafe.getLong(t, offset));
                        pos = pos6;
                        messageSchema = this;
                    }
                    break;
                case 3:
                    int pos8 = pos;
                    int pos9 = presenceMask;
                    if (!isFieldPresent(t, pos8, currentPresenceFieldOffset, currentPresenceField, pos9)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        pos = pos8;
                        messageSchema = this;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeUInt64(number, unsafe.getLong(t, offset));
                        pos = pos8;
                        messageSchema = this;
                    }
                    break;
                case 4:
                    int pos10 = pos;
                    int pos11 = presenceMask;
                    if (!isFieldPresent(t, pos10, currentPresenceFieldOffset, currentPresenceField, pos11)) {
                        messageSchema = this;
                        pos = pos10;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeInt32(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos10;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    int pos12 = pos;
                    int pos13 = presenceMask;
                    if (!isFieldPresent(t, pos12, currentPresenceFieldOffset, currentPresenceField, pos13)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        pos = pos12;
                        messageSchema = this;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeFixed64(number, unsafe.getLong(t, offset));
                        pos = pos12;
                        messageSchema = this;
                    }
                    break;
                case 6:
                    int pos14 = pos;
                    int pos15 = presenceMask;
                    if (!isFieldPresent(t, pos14, currentPresenceFieldOffset, currentPresenceField, pos15)) {
                        messageSchema = this;
                        pos = pos14;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeFixed32(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos14;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 7:
                    int pos16 = pos;
                    int pos17 = presenceMask;
                    if (!messageSchema.isFieldPresent(t, pos16, currentPresenceFieldOffset, currentPresenceField, pos17)) {
                        messageSchema = this;
                        pos = pos16;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeBool(number, booleanAt(t, offset));
                        messageSchema = this;
                        pos = pos16;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 8:
                    int pos18 = pos;
                    int pos19 = presenceMask;
                    t = message;
                    if (!messageSchema.isFieldPresent(t, pos18, currentPresenceFieldOffset, currentPresenceField, pos19)) {
                        pos = pos18;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        messageSchema.writeString(number, unsafe.getObject(t, offset), writer);
                        pos = pos18;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 9:
                    int pos20 = pos;
                    int pos21 = presenceMask;
                    messageSchema = this;
                    if (!messageSchema.isFieldPresent(t, pos20, currentPresenceFieldOffset, currentPresenceField, pos21)) {
                        t = message;
                        pos = pos20;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        Object value = unsafe.getObject(t, offset);
                        writer.writeMessage(number, value, messageSchema.getMessageFieldSchema(pos20));
                        t = message;
                        pos = pos20;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 10:
                    int pos22 = pos;
                    int pos23 = presenceMask;
                    if (!isFieldPresent(t, pos22, currentPresenceFieldOffset, currentPresenceField, pos23)) {
                        messageSchema = this;
                        pos = pos22;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeBytes(number, (ByteString) unsafe.getObject(t, offset));
                        messageSchema = this;
                        pos = pos22;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 11:
                    int pos24 = pos;
                    int pos25 = presenceMask;
                    if (!isFieldPresent(t, pos24, currentPresenceFieldOffset, currentPresenceField, pos25)) {
                        messageSchema = this;
                        pos = pos24;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeUInt32(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos24;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 12:
                    int pos26 = pos;
                    int pos27 = presenceMask;
                    if (!isFieldPresent(t, pos26, currentPresenceFieldOffset, currentPresenceField, pos27)) {
                        messageSchema = this;
                        pos = pos26;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeEnum(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos26;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 13:
                    int pos28 = pos;
                    int pos29 = presenceMask;
                    if (!isFieldPresent(t, pos28, currentPresenceFieldOffset, currentPresenceField, pos29)) {
                        messageSchema = this;
                        pos = pos28;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeSFixed32(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos28;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 14:
                    int pos30 = pos;
                    int pos31 = presenceMask;
                    if (!isFieldPresent(t, pos30, currentPresenceFieldOffset, currentPresenceField, pos31)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        pos = pos30;
                        messageSchema = this;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeSFixed64(number, unsafe.getLong(t, offset));
                        pos = pos30;
                        messageSchema = this;
                    }
                    break;
                case 15:
                    int pos32 = pos;
                    int pos33 = presenceMask;
                    if (!isFieldPresent(t, pos32, currentPresenceFieldOffset, currentPresenceField, pos33)) {
                        messageSchema = this;
                        pos = pos32;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeSInt32(number, unsafe.getInt(t, offset));
                        messageSchema = this;
                        pos = pos32;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 16:
                    int pos34 = pos;
                    int pos35 = presenceMask;
                    t = message;
                    if (!messageSchema.isFieldPresent(t, pos34, currentPresenceFieldOffset, currentPresenceField, pos35)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        pos = pos34;
                        messageSchema = this;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeSInt64(number, unsafe.getLong(t, offset));
                        pos = pos34;
                        messageSchema = this;
                    }
                    break;
                case 17:
                    int pos36 = pos;
                    int pos37 = presenceMask;
                    if (!messageSchema.isFieldPresent(t, pos36, currentPresenceFieldOffset, currentPresenceField, pos37)) {
                        t = message;
                        pos = pos36;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        writer.writeGroup(number, unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(pos36));
                        t = message;
                        pos = pos36;
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    }
                    break;
                case 18:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeDoubleList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 19:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFloatList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case OFFSET_BITS /* 20 */:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 21:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeUInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 22:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 23:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFixed64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 24:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFixed32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 25:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeBoolList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 26:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeStringList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer);
                    break;
                case 27:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeMessageList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, messageSchema.getMessageFieldSchema(pos));
                    currentPresenceField = currentPresenceField;
                    break;
                case 28:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeBytesList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer);
                    break;
                case 29:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeUInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 30:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeEnumList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 31:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSFixed32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 32:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSFixed64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 33:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 34:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, false);
                    currentPresenceField = currentPresenceField;
                    break;
                case 35:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeDoubleList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 36:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFloatList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 37:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 38:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeUInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 39:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 40:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFixed64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 41:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeFixed32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 42:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeBoolList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 43:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeUInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 44:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeEnumList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 45:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSFixed32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 46:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSFixed64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 47:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSInt32List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 48:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeSInt64List(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, z);
                    currentPresenceField = currentPresenceField;
                    break;
                case 49:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    SchemaUtil.writeGroupList(messageSchema.numberAt(pos), (List) unsafe.getObject(t, offset), writer, messageSchema.getMessageFieldSchema(pos));
                    currentPresenceField = currentPresenceField;
                    break;
                case 50:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    messageSchema.writeMapHelper(writer, number, unsafe.getObject(t, offset), pos);
                    break;
                case ONEOF_TYPE_OFFSET /* 51 */:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeDouble(number, oneofDoubleAt(t, offset));
                    }
                    break;
                case 52:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeFloat(number, oneofFloatAt(t, offset));
                    }
                    break;
                case 53:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeInt64(number, oneofLongAt(t, offset));
                    }
                    break;
                case 54:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeUInt64(number, oneofLongAt(t, offset));
                    }
                    break;
                case 55:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeInt32(number, oneofIntAt(t, offset));
                    }
                    break;
                case 56:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeFixed64(number, oneofLongAt(t, offset));
                    }
                    break;
                case 57:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeFixed32(number, oneofIntAt(t, offset));
                    }
                    break;
                case 58:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeBool(number, oneofBooleanAt(t, offset));
                    }
                    break;
                case 59:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        messageSchema.writeString(number, unsafe.getObject(t, offset), writer);
                    }
                    break;
                case 60:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        Object value2 = unsafe.getObject(t, offset);
                        writer.writeMessage(number, value2, messageSchema.getMessageFieldSchema(pos));
                    }
                    break;
                case 61:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeBytes(number, (ByteString) unsafe.getObject(t, offset));
                    }
                    break;
                case 62:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeUInt32(number, oneofIntAt(t, offset));
                    }
                    break;
                case 63:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeEnum(number, oneofIntAt(t, offset));
                    }
                    break;
                case 64:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeSFixed32(number, oneofIntAt(t, offset));
                    }
                    break;
                case 65:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeSFixed64(number, oneofLongAt(t, offset));
                    }
                    break;
                case 66:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeSInt32(number, oneofIntAt(t, offset));
                    }
                    break;
                case 67:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    if (messageSchema.isOneofPresent(t, number, pos)) {
                        writer.writeSInt64(number, oneofLongAt(t, offset));
                    }
                    break;
                case 68:
                    if (!messageSchema.isOneofPresent(t, number, pos)) {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    } else {
                        currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                        writer.writeGroup(number, unsafe.getObject(t, offset), messageSchema.getMessageFieldSchema(pos));
                    }
                    break;
                default:
                    currentPresenceFieldOffset2 = currentPresenceFieldOffset;
                    break;
            }
            pos += 3;
            entry2 = entry;
            extensionIterator = extensionIterator3;
            bufferLength = bufferLength2;
            currentPresenceFieldOffset3 = currentPresenceFieldOffset2;
        }
        Iterator<? extends Map.Entry<?, ?>> extensionIterator4 = extensionIterator;
        while (entry2 != null) {
            messageSchema.extensionSchema.serializeExtension(writer, entry2);
            entry2 = extensionIterator4.hasNext() ? (Map.Entry) extensionIterator4.next() : null;
        }
        messageSchema.writeUnknownInMessageTo(messageSchema.unknownFieldSchema, t, writer);
    }

    private void writeFieldsInDescendingOrder(T message, Writer writer) throws IOException {
        writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
        Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
        Map.Entry entry = null;
        if (this.hasExtensions) {
            FieldSet<T> extensions = this.extensionSchema.getExtensions(message);
            if (!extensions.isEmpty()) {
                extensionIterator = extensions.descendingIterator();
                Map.Entry nextExtension = extensionIterator.next();
                entry = nextExtension;
            }
        }
        int pos = this.buffer.length;
        while (true) {
            pos -= 3;
            if (pos >= 0) {
                int typeAndOffset = typeAndOffsetAt(pos);
                int number = numberAt(pos);
                while (entry != null && this.extensionSchema.extensionNumber(entry) > number) {
                    this.extensionSchema.serializeExtension(writer, entry);
                    entry = extensionIterator.hasNext() ? (Map.Entry) extensionIterator.next() : null;
                }
                switch (type(typeAndOffset)) {
                    case 0:
                        if (isFieldPresent(message, pos)) {
                            writer.writeDouble(number, doubleAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 1:
                        if (isFieldPresent(message, pos)) {
                            writer.writeFloat(number, floatAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 2:
                        if (isFieldPresent(message, pos)) {
                            writer.writeInt64(number, longAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 3:
                        if (isFieldPresent(message, pos)) {
                            writer.writeUInt64(number, longAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 4:
                        if (isFieldPresent(message, pos)) {
                            writer.writeInt32(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                        if (isFieldPresent(message, pos)) {
                            writer.writeFixed64(number, longAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 6:
                        if (isFieldPresent(message, pos)) {
                            writer.writeFixed32(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 7:
                        if (isFieldPresent(message, pos)) {
                            writer.writeBool(number, booleanAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 8:
                        if (isFieldPresent(message, pos)) {
                            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        }
                        break;
                    case 9:
                        if (isFieldPresent(message, pos)) {
                            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
                            writer.writeMessage(number, value, getMessageFieldSchema(pos));
                        }
                        break;
                    case 10:
                        if (isFieldPresent(message, pos)) {
                            writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                        }
                        break;
                    case 11:
                        if (isFieldPresent(message, pos)) {
                            writer.writeUInt32(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 12:
                        if (isFieldPresent(message, pos)) {
                            writer.writeEnum(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 13:
                        if (isFieldPresent(message, pos)) {
                            writer.writeSFixed32(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 14:
                        if (isFieldPresent(message, pos)) {
                            writer.writeSFixed64(number, longAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 15:
                        if (isFieldPresent(message, pos)) {
                            writer.writeSInt32(number, intAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 16:
                        if (isFieldPresent(message, pos)) {
                            writer.writeSInt64(number, longAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 17:
                        if (isFieldPresent(message, pos)) {
                            writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                        }
                        break;
                    case 18:
                        SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 19:
                        SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case OFFSET_BITS /* 20 */:
                        SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 21:
                        SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 22:
                        SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 23:
                        SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 24:
                        SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 25:
                        SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 26:
                        SchemaUtil.writeStringList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    case 27:
                        SchemaUtil.writeMessageList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                        break;
                    case 28:
                        SchemaUtil.writeBytesList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        break;
                    case 29:
                        SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 30:
                        SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 31:
                        SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 32:
                        SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 33:
                        SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 34:
                        SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
                        break;
                    case 35:
                        SchemaUtil.writeDoubleList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 36:
                        SchemaUtil.writeFloatList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 37:
                        SchemaUtil.writeInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 38:
                        SchemaUtil.writeUInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 39:
                        SchemaUtil.writeInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 40:
                        SchemaUtil.writeFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 41:
                        SchemaUtil.writeFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 42:
                        SchemaUtil.writeBoolList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 43:
                        SchemaUtil.writeUInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 44:
                        SchemaUtil.writeEnumList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 45:
                        SchemaUtil.writeSFixed32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 46:
                        SchemaUtil.writeSFixed64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 47:
                        SchemaUtil.writeSInt32List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 48:
                        SchemaUtil.writeSInt64List(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
                        break;
                    case 49:
                        SchemaUtil.writeGroupList(numberAt(pos), (List) UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, getMessageFieldSchema(pos));
                        break;
                    case 50:
                        writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
                        break;
                    case ONEOF_TYPE_OFFSET /* 51 */:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 52:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 53:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 54:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 55:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 56:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 57:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 58:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 59:
                        if (isOneofPresent(message, number, pos)) {
                            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
                        }
                        break;
                    case 60:
                        if (isOneofPresent(message, number, pos)) {
                            Object value2 = UnsafeUtil.getObject(message, offset(typeAndOffset));
                            writer.writeMessage(number, value2, getMessageFieldSchema(pos));
                        }
                        break;
                    case 61:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeBytes(number, (ByteString) UnsafeUtil.getObject(message, offset(typeAndOffset)));
                        }
                        break;
                    case 62:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 63:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 64:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 65:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 66:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 67:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset)));
                        }
                        break;
                    case 68:
                        if (isOneofPresent(message, number, pos)) {
                            writer.writeGroup(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), getMessageFieldSchema(pos));
                        }
                        break;
                }
            } else {
                while (entry != null) {
                    this.extensionSchema.serializeExtension(writer, entry);
                    entry = extensionIterator.hasNext() ? (Map.Entry) extensionIterator.next() : null;
                }
                return;
            }
        }
    }

    private <K, V> void writeMapHelper(Writer writer, int number, Object mapField, int pos) throws IOException {
        if (mapField != null) {
            writer.writeMap(number, this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(pos)), this.mapFieldSchema.forMapData(mapField));
        }
    }

    private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> schema, T message, Writer writer) throws IOException {
        schema.writeTo(schema.getFromMessage(message), writer);
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null) {
            throw new NullPointerException();
        }
        checkMutable(message);
        mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    /*  JADX ERROR: Type inference failed
        jadx.core.utils.exceptions.JadxOverflowException: Type inference error: updates count limit reached with updateSeq = 24661. Try increasing type updates limit count.
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:79)
        */
    private <UT, UB, ET extends com.google.protobuf.FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(com.google.protobuf.UnknownFieldSchema<UT, UB> r18, com.google.protobuf.ExtensionSchema<ET> r19, T r20, com.google.protobuf.Reader r21, com.google.protobuf.ExtensionRegistryLite r22) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 2466
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.mergeFromHelper(com.google.protobuf.UnknownFieldSchema, com.google.protobuf.ExtensionSchema, java.lang.Object, com.google.protobuf.Reader, com.google.protobuf.ExtensionRegistryLite):void");
    }

    static UnknownFieldSetLite getMutableUnknownFields(Object message) {
        UnknownFieldSetLite unknownFields = ((GeneratedMessageLite) message).unknownFields;
        if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            UnknownFieldSetLite unknownFields2 = UnknownFieldSetLite.newInstance();
            ((GeneratedMessageLite) message).unknownFields = unknownFields2;
            return unknownFields2;
        }
        return unknownFields;
    }

    /* JADX INFO: renamed from: com.google.protobuf.MessageSchema$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType = new int[WireFormat.FieldType.values().length];

        static {
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
        }
    }

    private int decodeMapEntryValue(byte[] data, int position, int limit, WireFormat.FieldType fieldType, Class<?> messageType, ArrayDecoders.Registers registers) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                int position2 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Boolean.valueOf(registers.long1 != 0);
                return position2;
            case 2:
                return ArrayDecoders.decodeBytes(data, position, registers);
            case 3:
                registers.object1 = Double.valueOf(ArrayDecoders.decodeDouble(data, position));
                return position + 8;
            case 4:
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                registers.object1 = Integer.valueOf(ArrayDecoders.decodeFixed32(data, position));
                return position + 4;
            case 6:
            case 7:
                registers.object1 = Long.valueOf(ArrayDecoders.decodeFixed64(data, position));
                return position + 8;
            case 8:
                registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(data, position));
                return position + 4;
            case 9:
            case 10:
            case 11:
                int position3 = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = Integer.valueOf(registers.int1);
                return position3;
            case 12:
            case 13:
                int position4 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Long.valueOf(registers.long1);
                return position4;
            case 14:
                return ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) messageType), data, position, limit, registers);
            case 15:
                int position5 = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
                return position5;
            case 16:
                int position6 = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
                return position6;
            case 17:
                return ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <K, V> int decodeMapEntry(byte[] data, int position, int limit, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map, ArrayDecoders.Registers registers) throws IOException {
        int tag;
        byte[] bArr = data;
        int position2 = ArrayDecoders.decodeVarint32(bArr, position, registers);
        int length = registers.int1;
        if (length < 0 || length > limit - position2) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        int end = position2 + length;
        K key = metadata.defaultKey;
        Object obj = key;
        Object obj2 = metadata.defaultValue;
        while (position2 < end) {
            int position3 = position2 + 1;
            int tag2 = bArr[position2];
            if (tag2 >= 0) {
                tag = tag2;
            } else {
                position3 = ArrayDecoders.decodeVarint32(tag2, bArr, position3, registers);
                tag = registers.int1;
            }
            int fieldNumber = tag >>> 3;
            int wireType = tag & 7;
            switch (fieldNumber) {
                case 1:
                    if (wireType != metadata.keyType.getWireType()) {
                        bArr = data;
                        position2 = ArrayDecoders.skipField(tag, bArr, position3, limit, registers);
                    } else {
                        bArr = data;
                        int position4 = decodeMapEntryValue(bArr, position3, limit, metadata.keyType, null, registers);
                        obj = registers.object1;
                        position2 = position4;
                    }
                    break;
                case 2:
                    if (wireType != metadata.valueType.getWireType()) {
                        bArr = data;
                        position2 = ArrayDecoders.skipField(tag, bArr, position3, limit, registers);
                    } else {
                        int position5 = decodeMapEntryValue(bArr, position3, limit, metadata.valueType, metadata.defaultValue.getClass(), registers);
                        obj2 = registers.object1;
                        bArr = data;
                        position2 = position5;
                    }
                    break;
                default:
                    position2 = ArrayDecoders.skipField(tag, bArr, position3, limit, registers);
                    break;
            }
        }
        if (position2 != end) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        map.put(obj, obj2);
        return end;
    }

    private int parseRepeatedField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int bufferPosition, long typeAndOffset, int fieldType, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Internal.ProtobufList<?> list;
        int position2;
        Internal.ProtobufList<?> list2 = (Internal.ProtobufList) UNSAFE.getObject(message, fieldOffset);
        if (list2.isModifiable()) {
            list = list2;
        } else {
            int size = list2.size();
            Internal.ProtobufList<?> list3 = list2.mutableCopyWithCapacity2(size == 0 ? 10 : size * 2);
            UNSAFE.putObject(message, fieldOffset, list3);
            list = list3;
        }
        switch (fieldType) {
            case 18:
            case 35:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedDoubleList(data, position, list, registers);
                }
                if (wireType == 1) {
                    return ArrayDecoders.decodeDoubleList(tag, data, position, limit, list, registers);
                }
                break;
            case 19:
            case 36:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFloatList(data, position, list, registers);
                }
                if (wireType == 5) {
                    return ArrayDecoders.decodeFloatList(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case OFFSET_BITS /* 20 */:
            case 21:
            case 37:
            case 38:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedVarint64List(data, position, list, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeVarint64List(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case 22:
            case 29:
            case 39:
            case 43:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
                }
                break;
            case 23:
            case 32:
            case 40:
            case 46:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFixed64List(data, position, list, registers);
                }
                if (wireType == 1) {
                    return ArrayDecoders.decodeFixed64List(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case 24:
            case 31:
            case 41:
            case 45:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedFixed32List(data, position, list, registers);
                }
                if (wireType == 5) {
                    return ArrayDecoders.decodeFixed32List(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case 25:
            case 42:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedBoolList(data, position, list, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeBoolList(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case 26:
                if (wireType == 2) {
                    if ((typeAndOffset & 536870912) == 0) {
                        return ArrayDecoders.decodeStringList(tag, data, position, limit, list, registers);
                    }
                    return ArrayDecoders.decodeStringListRequireUtf8(tag, data, position, limit, list, registers);
                }
                break;
            case 27:
                if (wireType == 2) {
                    return ArrayDecoders.decodeMessageList(getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
                }
                break;
            case 28:
                if (wireType == 2) {
                    return ArrayDecoders.decodeBytesList(tag, data, position, limit, list, registers);
                }
                break;
            case 30:
            case 44:
                if (wireType == 2) {
                    position2 = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                } else if (wireType == 0) {
                    Internal.ProtobufList<?> list4 = list;
                    list = list4;
                    position2 = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list4, registers);
                }
                SchemaUtil.filterUnknownEnumList((Object) message, number, (List<Integer>) list, getEnumFieldVerifier(bufferPosition), (Object) null, (UnknownFieldSchema<UT, Object>) this.unknownFieldSchema);
                return position2;
            case 33:
            case 47:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedSInt32List(data, position, list, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeSInt32List(tag, data, position, limit, list, registers);
                }
                break;
            case 34:
            case 48:
                if (wireType == 2) {
                    return ArrayDecoders.decodePackedSInt64List(data, position, list, registers);
                }
                if (wireType == 0) {
                    return ArrayDecoders.decodeSInt64List(tag, data, position, limit, list, registers);
                }
                break;
                break;
            case 49:
                if (wireType == 3) {
                    return ArrayDecoders.decodeGroupList(getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
                }
                break;
        }
        return position;
    }

    private <K, V> int parseMapField(T message, byte[] data, int position, int limit, int bufferPosition, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Object mapField;
        Unsafe unsafe = UNSAFE;
        Object mapDefaultEntry = getMapFieldDefaultEntry(bufferPosition);
        Object mapField2 = unsafe.getObject(message, fieldOffset);
        if (!this.mapFieldSchema.isImmutable(mapField2)) {
            mapField = mapField2;
        } else {
            Object mapField3 = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField3, mapField2);
            unsafe.putObject(message, fieldOffset, mapField3);
            mapField = mapField3;
        }
        return decodeMapEntry(data, position, limit, this.mapFieldSchema.forMapMetadata(mapDefaultEntry), this.mapFieldSchema.forMutableMapData(mapField), registers);
    }

    private int parseOneofField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int typeAndOffset, int fieldType, long fieldOffset, int bufferPosition, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe = UNSAFE;
        long oneofCaseOffset = this.buffer[bufferPosition + 2] & 1048575;
        switch (fieldType) {
            case ONEOF_TYPE_OFFSET /* 51 */:
                if (wireType != 1) {
                    return position;
                }
                unsafe.putObject(message, fieldOffset, Double.valueOf(ArrayDecoders.decodeDouble(data, position)));
                int position2 = position + 8;
                unsafe.putInt(message, oneofCaseOffset, number);
                return position2;
            case 52:
                if (wireType != 5) {
                    return position;
                }
                unsafe.putObject(message, fieldOffset, Float.valueOf(ArrayDecoders.decodeFloat(data, position)));
                int position3 = position + 4;
                unsafe.putInt(message, oneofCaseOffset, number);
                return position3;
            case 53:
            case 54:
                if (wireType != 0) {
                    return position;
                }
                int position4 = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, Long.valueOf(registers.long1));
                unsafe.putInt(message, oneofCaseOffset, number);
                return position4;
            case 55:
            case 62:
                if (wireType != 0) {
                    return position;
                }
                int position5 = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putObject(message, fieldOffset, Integer.valueOf(registers.int1));
                unsafe.putInt(message, oneofCaseOffset, number);
                return position5;
            case 56:
            case 65:
                if (wireType != 1) {
                    return position;
                }
                unsafe.putObject(message, fieldOffset, Long.valueOf(ArrayDecoders.decodeFixed64(data, position)));
                int position6 = position + 8;
                unsafe.putInt(message, oneofCaseOffset, number);
                return position6;
            case 57:
            case 64:
                if (wireType != 5) {
                    return position;
                }
                unsafe.putObject(message, fieldOffset, Integer.valueOf(ArrayDecoders.decodeFixed32(data, position)));
                int position7 = position + 4;
                unsafe.putInt(message, oneofCaseOffset, number);
                return position7;
            case 58:
                if (wireType != 0) {
                    return position;
                }
                int position8 = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, Boolean.valueOf(registers.long1 != 0));
                unsafe.putInt(message, oneofCaseOffset, number);
                return position8;
            case 59:
                if (wireType != 2) {
                    return position;
                }
                int position9 = ArrayDecoders.decodeVarint32(data, position, registers);
                int length = registers.int1;
                if (length == 0) {
                    unsafe.putObject(message, fieldOffset, "");
                } else {
                    if ((typeAndOffset & ENFORCE_UTF8_MASK) != 0 && !Utf8.isValidUtf8(data, position9, position9 + length)) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    String value = new String(data, position9, length, Internal.UTF_8);
                    unsafe.putObject(message, fieldOffset, value);
                    position9 += length;
                }
                unsafe.putInt(message, oneofCaseOffset, number);
                return position9;
            case 60:
                if (wireType != 2) {
                    return position;
                }
                Object current = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                int position10 = ArrayDecoders.mergeMessageField(current, getMessageFieldSchema(bufferPosition), data, position, limit, registers);
                storeOneofMessageField(message, number, bufferPosition, current);
                return position10;
            case 61:
                if (wireType != 2) {
                    return position;
                }
                int position11 = ArrayDecoders.decodeBytes(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.object1);
                unsafe.putInt(message, oneofCaseOffset, number);
                return position11;
            case 63:
                if (wireType != 0) {
                    return position;
                }
                int position12 = ArrayDecoders.decodeVarint32(data, position, registers);
                int enumValue = registers.int1;
                Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(bufferPosition);
                if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                    unsafe.putObject(message, fieldOffset, Integer.valueOf(enumValue));
                    unsafe.putInt(message, oneofCaseOffset, number);
                } else {
                    position12 = position12;
                    getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
                }
                return position12;
            case 66:
                if (wireType != 0) {
                    return position;
                }
                int position13 = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putObject(message, fieldOffset, Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)));
                unsafe.putInt(message, oneofCaseOffset, number);
                return position13;
            case 67:
                if (wireType != 0) {
                    return position;
                }
                int position14 = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)));
                unsafe.putInt(message, oneofCaseOffset, number);
                return position14;
            case 68:
                if (wireType != 3) {
                    return position;
                }
                Object current2 = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                int endTag = (tag & (-8)) | 4;
                int position15 = ArrayDecoders.mergeGroupField(current2, getMessageFieldSchema(bufferPosition), data, position, limit, endTag, registers);
                storeOneofMessageField(message, number, bufferPosition, current2);
                return position15;
            default:
                return position;
        }
    }

    private Schema getMessageFieldSchema(int pos) {
        int index = (pos / 3) * 2;
        Schema schema = (Schema) this.objects[index];
        if (schema != null) {
            return schema;
        }
        Schema<T> schemaSchemaFor = Protobuf.getInstance().schemaFor((Class) this.objects[index + 1]);
        this.objects[index] = schemaSchemaFor;
        return schemaSchemaFor;
    }

    private Object getMapFieldDefaultEntry(int pos) {
        return this.objects[(pos / 3) * 2];
    }

    private Internal.EnumVerifier getEnumFieldVerifier(int pos) {
        return (Internal.EnumVerifier) this.objects[((pos / 3) * 2) + 1];
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:29:0x00a9. Please report as an issue. */
    int parseMessage(T message, byte[] data, int position, int limit, int endDelimited, ArrayDecoders.Registers registers) throws IOException {
        int typeAndOffset;
        T t;
        int i;
        Unsafe unsafe;
        MessageSchema<T> messageSchema;
        int currentPresenceFieldOffset;
        int number;
        int tag;
        int wireType;
        int position2;
        int tag2;
        int i2;
        int pos;
        int position3;
        int currentPresenceField;
        int tag3;
        int tag4;
        int position4;
        int i3;
        int currentPresenceField2;
        int fieldType;
        int wireType2;
        Unsafe unsafe2;
        int currentPresenceFieldOffset2;
        int position5;
        int currentPresenceFieldOffset3;
        int position6;
        int currentPresenceFieldOffset4;
        int wireType3;
        MessageSchema<T> messageSchema2 = this;
        T t2 = message;
        byte[] bArr = data;
        int fieldType2 = limit;
        ArrayDecoders.Registers registers2 = registers;
        checkMutable(t2);
        Unsafe unsafe3 = UNSAFE;
        int tag5 = 0;
        int wireType4 = 0;
        int number2 = 0;
        int oldNumber = -1;
        int pos2 = 1048575;
        int position7 = position;
        while (true) {
            if (position7 >= fieldType2) {
                typeAndOffset = endDelimited;
                t = t2;
                i = fieldType2;
                int currentPresenceFieldOffset5 = pos2;
                unsafe = unsafe3;
                int currentPresenceField3 = number2;
                messageSchema = messageSchema2;
                currentPresenceFieldOffset = tag5;
                number = currentPresenceFieldOffset5;
                tag = position7;
                wireType = currentPresenceField3;
            } else {
                int position8 = position7 + 1;
                int tag6 = bArr[position7];
                if (tag6 >= 0) {
                    position2 = tag6;
                    tag2 = position8;
                } else {
                    int position9 = ArrayDecoders.decodeVarint32(tag6, bArr, position8, registers2);
                    int tag7 = registers2.int1;
                    position2 = tag7;
                    tag2 = position9;
                }
                int number3 = position2 >>> 3;
                int wireType5 = position2 & 7;
                if (number3 > oldNumber) {
                    i2 = 1048575;
                    int pos3 = messageSchema2.positionForFieldNumber(number3, wireType4 / 3);
                    pos = pos3;
                } else {
                    i2 = 1048575;
                    int pos4 = messageSchema2.positionForFieldNumber(number3);
                    pos = pos4;
                }
                oldNumber = number3;
                if (pos == -1) {
                    position3 = tag2;
                    pos = 0;
                    unsafe = unsafe3;
                    currentPresenceField = number2;
                    tag3 = position2;
                    messageSchema = messageSchema2;
                } else {
                    int typeAndOffset2 = messageSchema2.buffer[pos + 1];
                    int fieldType3 = type(typeAndOffset2);
                    long fieldOffset = offset(typeAndOffset2);
                    int position10 = tag2;
                    if (fieldType3 <= 17) {
                        int presenceMaskAndOffset = messageSchema2.buffer[pos + 2];
                        int presenceMask = 1 << (presenceMaskAndOffset >>> OFFSET_BITS);
                        int presenceFieldOffset = presenceMaskAndOffset & i2;
                        if (presenceFieldOffset == pos2) {
                            fieldType3 = fieldType3;
                            currentPresenceField = number2;
                            currentPresenceField2 = pos2;
                        } else {
                            if (pos2 != i2) {
                                unsafe3.putInt(t2, pos2, number2);
                            }
                            currentPresenceField2 = presenceFieldOffset;
                            currentPresenceField = presenceFieldOffset == 1048575 ? 0 : unsafe3.getInt(t2, presenceFieldOffset);
                        }
                        switch (fieldType3) {
                            case 0:
                                T t3 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                currentPresenceFieldOffset2 = position10;
                                if (wireType2 == 1) {
                                    UnsafeUtil.putDouble(t3, fieldOffset, ArrayDecoders.decodeDouble(data, currentPresenceFieldOffset2));
                                    position7 = currentPresenceFieldOffset2 + 8;
                                    number2 = currentPresenceField | presenceMask;
                                    fieldType2 = limit;
                                    bArr = data;
                                    t2 = t3;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 1:
                                T t4 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                currentPresenceFieldOffset2 = position10;
                                if (wireType2 == 5) {
                                    UnsafeUtil.putFloat(t4, fieldOffset, ArrayDecoders.decodeFloat(data, currentPresenceFieldOffset2));
                                    position7 = currentPresenceFieldOffset2 + 4;
                                    number2 = currentPresenceField | presenceMask;
                                    fieldType2 = limit;
                                    bArr = data;
                                    t2 = t4;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 2:
                            case 3:
                                unsafe2 = unsafe3;
                                fieldType = currentPresenceField2;
                                currentPresenceFieldOffset2 = position10;
                                wireType2 = wireType5;
                                if (wireType2 == 0) {
                                    int position11 = ArrayDecoders.decodeVarint64(data, currentPresenceFieldOffset2, registers2);
                                    T t5 = t2;
                                    unsafe2.putLong(t5, fieldOffset, registers2.long1);
                                    number2 = currentPresenceField | presenceMask;
                                    position7 = position11;
                                    fieldType2 = limit;
                                    bArr = data;
                                    t2 = t5;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 4:
                            case 11:
                                unsafe2 = unsafe3;
                                fieldType = currentPresenceField2;
                                currentPresenceFieldOffset2 = position10;
                                wireType2 = wireType5;
                                if (wireType2 == 0) {
                                    int position12 = ArrayDecoders.decodeVarint32(data, currentPresenceFieldOffset2, registers2);
                                    unsafe2.putInt(t2, fieldOffset, registers2.int1);
                                    number2 = currentPresenceField | presenceMask;
                                    fieldType2 = limit;
                                    position7 = position12;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                            case 14:
                                ArrayDecoders.Registers registers3 = registers2;
                                T t6 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                if (wireType2 == 1) {
                                    registers2 = registers3;
                                    unsafe2.putLong(t6, fieldOffset, ArrayDecoders.decodeFixed64(data, position10));
                                    t2 = t6;
                                    number2 = currentPresenceField | presenceMask;
                                    fieldType2 = limit;
                                    position7 = position10 + 8;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    registers2 = registers3;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 6:
                            case 13:
                                ArrayDecoders.Registers registers4 = registers2;
                                T t7 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                if (wireType2 == 5) {
                                    unsafe2.putInt(t7, fieldOffset, ArrayDecoders.decodeFixed32(data, position10));
                                    position7 = position10 + 4;
                                    int position13 = currentPresenceField | presenceMask;
                                    registers2 = registers4;
                                    t2 = t7;
                                    fieldType2 = limit;
                                    number2 = position13;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    registers2 = registers4;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 7:
                                ArrayDecoders.Registers registers5 = registers2;
                                T t8 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                if (wireType2 == 0) {
                                    position7 = ArrayDecoders.decodeVarint64(data, position10, registers5);
                                    UnsafeUtil.putBoolean(t8, fieldOffset, registers5.long1 != 0);
                                    registers2 = registers5;
                                    t2 = t8;
                                    fieldType2 = limit;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    registers2 = registers5;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 8:
                                ArrayDecoders.Registers registers6 = registers2;
                                T t9 = t2;
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                if (wireType2 == 2) {
                                    if (isEnforceUtf8(typeAndOffset2)) {
                                        position5 = ArrayDecoders.decodeStringRequireUtf8(data, position10, registers6);
                                    } else {
                                        position5 = ArrayDecoders.decodeString(data, position10, registers6);
                                    }
                                    position7 = position5;
                                    unsafe2.putObject(t9, fieldOffset, registers6.object1);
                                    registers2 = registers6;
                                    t2 = t9;
                                    fieldType2 = limit;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe2;
                                    pos2 = fieldType;
                                } else {
                                    registers2 = registers6;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 9:
                                int position14 = currentPresenceField2;
                                fieldType = position14;
                                ArrayDecoders.Registers registers7 = registers2;
                                Unsafe unsafe4 = unsafe3;
                                wireType2 = wireType5;
                                if (wireType2 == 2) {
                                    T t10 = t2;
                                    Object current = messageSchema2.mutableMessageFieldForMerge(t10, pos);
                                    int position15 = ArrayDecoders.mergeMessageField(current, messageSchema2.getMessageFieldSchema(pos), data, position10, limit, registers7);
                                    messageSchema2.storeMessageField(t10, pos, current);
                                    registers2 = registers7;
                                    t2 = t10;
                                    position7 = position15;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe4;
                                    pos2 = fieldType;
                                    fieldType2 = limit;
                                } else {
                                    unsafe2 = unsafe4;
                                    registers2 = registers7;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 10:
                                int position16 = currentPresenceField2;
                                fieldType = position16;
                                ArrayDecoders.Registers registers8 = registers2;
                                Unsafe unsafe5 = unsafe3;
                                wireType2 = wireType5;
                                if (wireType2 != 2) {
                                    registers2 = registers8;
                                    unsafe2 = unsafe5;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                } else {
                                    position7 = ArrayDecoders.decodeBytes(data, position10, registers8);
                                    unsafe5.putObject(t2, fieldOffset, registers8.object1);
                                    fieldType2 = limit;
                                    unsafe3 = unsafe5;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    registers2 = registers8;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    pos2 = fieldType;
                                }
                                break;
                            case 12:
                                int position17 = currentPresenceField2;
                                fieldType = position17;
                                ArrayDecoders.Registers registers9 = registers2;
                                Unsafe unsafe6 = unsafe3;
                                wireType2 = wireType5;
                                if (wireType2 == 0) {
                                    int position18 = ArrayDecoders.decodeVarint32(data, position10, registers9);
                                    int enumValue = registers9.int1;
                                    Internal.EnumVerifier enumVerifier = messageSchema2.getEnumFieldVerifier(pos);
                                    if (!isLegacyEnumIsClosed(typeAndOffset2) || enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                                        unsafe6.putInt(t2, fieldOffset, enumValue);
                                        fieldType2 = limit;
                                        unsafe3 = unsafe6;
                                        number2 = currentPresenceField | presenceMask;
                                        bArr = data;
                                        registers2 = registers9;
                                        wireType4 = pos;
                                        tag5 = position2;
                                        position7 = position18;
                                        pos2 = fieldType;
                                    } else {
                                        getMutableUnknownFields(t2).storeField(position2, Long.valueOf(enumValue));
                                        fieldType2 = limit;
                                        unsafe3 = unsafe6;
                                        bArr = data;
                                        registers2 = registers9;
                                        wireType4 = pos;
                                        tag5 = position2;
                                        position7 = position18;
                                        number2 = currentPresenceField;
                                        pos2 = fieldType;
                                    }
                                } else {
                                    registers2 = registers9;
                                    unsafe2 = unsafe6;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 15:
                                int position19 = currentPresenceField2;
                                fieldType = position19;
                                ArrayDecoders.Registers registers10 = registers2;
                                Unsafe unsafe7 = unsafe3;
                                wireType2 = wireType5;
                                if (wireType2 != 0) {
                                    registers2 = registers10;
                                    unsafe2 = unsafe7;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                } else {
                                    position7 = ArrayDecoders.decodeVarint32(data, position10, registers10);
                                    unsafe7.putInt(t2, fieldOffset, CodedInputStream.decodeZigZag32(registers10.int1));
                                    fieldType2 = limit;
                                    unsafe3 = unsafe7;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    registers2 = registers10;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    pos2 = fieldType;
                                }
                                break;
                            case 16:
                                int position20 = currentPresenceField2;
                                fieldType = position20;
                                ArrayDecoders.Registers registers11 = registers2;
                                Unsafe unsafe8 = unsafe3;
                                wireType2 = wireType5;
                                if (wireType2 == 0) {
                                    int position21 = ArrayDecoders.decodeVarint64(data, position10, registers11);
                                    T t11 = t2;
                                    unsafe8.putLong(t11, fieldOffset, CodedInputStream.decodeZigZag64(registers11.long1));
                                    t2 = t11;
                                    fieldType2 = limit;
                                    unsafe3 = unsafe8;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    registers2 = registers11;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    position7 = position21;
                                    pos2 = fieldType;
                                } else {
                                    registers2 = registers11;
                                    unsafe2 = unsafe8;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                }
                                break;
                            case 17:
                                if (wireType5 != 3) {
                                    int position22 = currentPresenceField2;
                                    fieldType = position22;
                                    Unsafe unsafe9 = unsafe3;
                                    wireType2 = wireType5;
                                    registers2 = registers2;
                                    unsafe2 = unsafe9;
                                    currentPresenceFieldOffset2 = position10;
                                    pos2 = fieldType;
                                    position3 = currentPresenceFieldOffset2;
                                    tag3 = position2;
                                    messageSchema = messageSchema2;
                                    unsafe = unsafe2;
                                } else {
                                    Object current2 = messageSchema2.mutableMessageFieldForMerge(t2, pos);
                                    int endTag = (number3 << 3) | 4;
                                    int currentPresenceFieldOffset6 = currentPresenceField2;
                                    ArrayDecoders.Registers registers12 = registers2;
                                    position7 = ArrayDecoders.mergeGroupField(current2, messageSchema2.getMessageFieldSchema(pos), data, position10, limit, endTag, registers12);
                                    messageSchema2.storeMessageField(t2, pos, current2);
                                    fieldType2 = limit;
                                    number2 = currentPresenceField | presenceMask;
                                    bArr = data;
                                    registers2 = registers12;
                                    wireType4 = pos;
                                    tag5 = position2;
                                    unsafe3 = unsafe3;
                                    pos2 = currentPresenceFieldOffset6;
                                }
                                break;
                            default:
                                unsafe2 = unsafe3;
                                wireType2 = wireType5;
                                fieldType = currentPresenceField2;
                                currentPresenceFieldOffset2 = position10;
                                pos2 = fieldType;
                                position3 = currentPresenceFieldOffset2;
                                tag3 = position2;
                                messageSchema = messageSchema2;
                                unsafe = unsafe2;
                                break;
                        }
                    } else {
                        currentPresenceField = number2;
                        T t12 = t2;
                        Unsafe unsafe10 = unsafe3;
                        if (fieldType3 == 27) {
                            if (wireType5 != 2) {
                                currentPresenceFieldOffset3 = pos2;
                                position6 = position10;
                                currentPresenceFieldOffset4 = pos;
                                int tag8 = position2;
                                wireType3 = tag8;
                                unsafe = unsafe10;
                                registers2 = registers;
                                position3 = position6;
                                pos = currentPresenceFieldOffset4;
                                tag3 = wireType3;
                                pos2 = currentPresenceFieldOffset3;
                                messageSchema = this;
                            } else {
                                Internal.ProtobufList<?> list = (Internal.ProtobufList) unsafe10.getObject(t12, fieldOffset);
                                if (!list.isModifiable()) {
                                    int size = list.size();
                                    list = list.mutableCopyWithCapacity2(size == 0 ? 10 : size * 2);
                                    unsafe10.putObject(t12, fieldOffset, list);
                                }
                                int currentPresenceFieldOffset7 = pos2;
                                int currentPresenceFieldOffset8 = pos;
                                int tag9 = position2;
                                int position23 = ArrayDecoders.decodeMessageList(messageSchema2.getMessageFieldSchema(pos), tag9, data, position10, limit, list, registers2);
                                bArr = data;
                                fieldType2 = limit;
                                registers2 = registers;
                                position7 = position23;
                                tag5 = tag9;
                                t2 = t12;
                                unsafe3 = unsafe10;
                                number2 = currentPresenceField;
                                wireType4 = currentPresenceFieldOffset8;
                                pos2 = currentPresenceFieldOffset7;
                            }
                        } else {
                            currentPresenceFieldOffset3 = pos2;
                            position6 = position10;
                            currentPresenceFieldOffset4 = pos;
                            int tag10 = position2;
                            if (fieldType3 <= 49) {
                                unsafe = unsafe10;
                                position7 = messageSchema2.parseRepeatedField(t12, data, position6, limit, tag10, number3, wireType5, currentPresenceFieldOffset4, typeAndOffset2, fieldType3, fieldOffset, registers);
                                if (position7 != position6) {
                                    messageSchema2 = this;
                                    t2 = message;
                                    bArr = data;
                                    fieldType2 = limit;
                                    registers2 = registers;
                                    wireType4 = currentPresenceFieldOffset4;
                                    tag5 = tag10;
                                    pos2 = currentPresenceFieldOffset3;
                                    number2 = currentPresenceField;
                                    unsafe3 = unsafe;
                                } else {
                                    registers2 = registers;
                                    position3 = position7;
                                    pos = currentPresenceFieldOffset4;
                                    tag3 = tag10;
                                    pos2 = currentPresenceFieldOffset3;
                                    messageSchema = this;
                                }
                            } else {
                                wireType3 = tag10;
                                unsafe = unsafe10;
                                if (fieldType3 == 50) {
                                    if (wireType5 == 2) {
                                        position7 = parseMapField(message, data, position6, limit, currentPresenceFieldOffset4, fieldOffset, registers);
                                        if (position7 != position6) {
                                            messageSchema2 = this;
                                            t2 = message;
                                            bArr = data;
                                            fieldType2 = limit;
                                            registers2 = registers;
                                            wireType4 = currentPresenceFieldOffset4;
                                            tag5 = wireType3;
                                            pos2 = currentPresenceFieldOffset3;
                                            number2 = currentPresenceField;
                                            unsafe3 = unsafe;
                                        } else {
                                            registers2 = registers;
                                            position3 = position7;
                                            pos = currentPresenceFieldOffset4;
                                            tag3 = wireType3;
                                            pos2 = currentPresenceFieldOffset3;
                                            messageSchema = this;
                                        }
                                    } else {
                                        registers2 = registers;
                                        position3 = position6;
                                        pos = currentPresenceFieldOffset4;
                                        tag3 = wireType3;
                                        pos2 = currentPresenceFieldOffset3;
                                        messageSchema = this;
                                    }
                                } else {
                                    tag3 = wireType3;
                                    position7 = parseOneofField(message, data, position6, limit, tag3, number3, wireType5, typeAndOffset2, fieldType3, fieldOffset, currentPresenceFieldOffset4, registers);
                                    messageSchema = this;
                                    registers2 = registers;
                                    if (position7 == position6) {
                                        position3 = position7;
                                        pos = currentPresenceFieldOffset4;
                                        pos2 = currentPresenceFieldOffset3;
                                    } else {
                                        t2 = message;
                                        bArr = data;
                                        fieldType2 = limit;
                                        tag5 = tag3;
                                        wireType4 = currentPresenceFieldOffset4;
                                        messageSchema2 = messageSchema;
                                        pos2 = currentPresenceFieldOffset3;
                                        number2 = currentPresenceField;
                                        unsafe3 = unsafe;
                                    }
                                }
                            }
                        }
                    }
                }
                typeAndOffset = endDelimited;
                if (tag3 == typeAndOffset && typeAndOffset != 0) {
                    t = message;
                    i = limit;
                    tag = position3;
                    number = pos2;
                    currentPresenceFieldOffset = tag3;
                    wireType = currentPresenceField;
                } else {
                    if (messageSchema.hasExtensions && registers2.extensionRegistry != ExtensionRegistryLite.getEmptyRegistry()) {
                        tag4 = tag3;
                        position4 = ArrayDecoders.decodeExtensionOrUnknownField(tag4, data, position3, limit, message, messageSchema.defaultInstance, messageSchema.unknownFieldSchema, registers2);
                        message = message;
                        i3 = limit;
                    } else {
                        tag4 = tag3;
                        position4 = ArrayDecoders.decodeUnknownField(tag4, data, position3, limit, getMutableUnknownFields(message), registers);
                        i3 = limit;
                    }
                    position7 = position4;
                    bArr = data;
                    tag5 = tag4;
                    t2 = message;
                    messageSchema2 = messageSchema;
                    wireType4 = pos;
                    fieldType2 = i3;
                    number2 = currentPresenceField;
                    unsafe3 = unsafe;
                    registers2 = registers;
                }
            }
        }
        if (number != 1048575) {
            unsafe.putInt(t, number, wireType);
        }
        UnknownFieldSetLite unknownFields = null;
        for (int i4 = messageSchema.checkInitializedCount; i4 < messageSchema.repeatedFieldOffsetStart; i4++) {
            T t13 = t;
            unknownFields = (UnknownFieldSetLite) messageSchema.filterMapUnknownEnumValues(t13, messageSchema.intArray[i4], unknownFields, messageSchema.unknownFieldSchema, message);
            t = t13;
        }
        T t14 = t;
        MessageSchema<T> messageSchema3 = messageSchema;
        if (unknownFields != null) {
            messageSchema3.unknownFieldSchema.setBuilderToMessage(t14, unknownFields);
        }
        if (typeAndOffset == 0) {
            if (tag != i) {
                throw InvalidProtocolBufferException.parseFailure();
            }
        } else if (tag > i || currentPresenceFieldOffset != typeAndOffset) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        return tag;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object mutableMessageFieldForMerge(T message, int pos) {
        Schema messageFieldSchema = getMessageFieldSchema(pos);
        long offset = offset(typeAndOffsetAt(pos));
        if (!isFieldPresent(message, pos)) {
            return messageFieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, offset);
        if (isMutable(current)) {
            return current;
        }
        Object newMessage = messageFieldSchema.newInstance();
        if (current != null) {
            messageFieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeMessageField(T message, int pos, Object field) {
        UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
        setFieldPresent(message, pos);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object mutableOneofMessageFieldForMerge(T message, int fieldNumber, int pos) {
        Schema messageFieldSchema = getMessageFieldSchema(pos);
        if (!isOneofPresent(message, fieldNumber, pos)) {
            return messageFieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, offset(typeAndOffsetAt(pos)));
        if (isMutable(current)) {
            return current;
        }
        Object newMessage = messageFieldSchema.newInstance();
        if (current != null) {
            messageFieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeOneofMessageField(T message, int fieldNumber, int pos, Object field) {
        UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
        setOneofPresent(message, fieldNumber, pos);
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        parseMessage(message, data, position, limit, 0, registers);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.protobuf.Schema
    public void makeImmutable(T t) {
        if (!isMutable(t)) {
            return;
        }
        if (t instanceof GeneratedMessageLite) {
            GeneratedMessageLite<?, ?> generatedMessage = (GeneratedMessageLite) t;
            generatedMessage.clearMemoizedSerializedSize();
            generatedMessage.clearMemoizedHashCode();
            generatedMessage.markImmutable();
        }
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = typeAndOffsetAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 9:
                case 17:
                    if (isFieldPresent(t, pos)) {
                        getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(t, offset));
                    }
                    break;
                case 18:
                case 19:
                case OFFSET_BITS /* 20 */:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    this.listFieldSchema.makeImmutableListAt(t, offset);
                    break;
                case 50:
                    Object mapField = UNSAFE.getObject(t, offset);
                    if (mapField != null) {
                        UNSAFE.putObject(t, offset, this.mapFieldSchema.toImmutable(mapField));
                    }
                    break;
                case 60:
                case 68:
                    if (isOneofPresent(t, numberAt(pos), pos)) {
                        getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(t, offset));
                    }
                    break;
            }
        }
        this.unknownFieldSchema.makeImmutable(t);
        if (this.hasExtensions) {
            this.extensionSchema.makeImmutable(t);
        }
    }

    private final <K, V> void mergeMap(Object message, int pos, Object mapDefaultEntry, ExtensionRegistryLite extensionRegistry, Reader reader) throws IOException {
        long offset = offset(typeAndOffsetAt(pos));
        Object mapField = UnsafeUtil.getObject(message, offset);
        if (mapField == null) {
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            UnsafeUtil.putObject(message, offset, mapField);
        } else if (this.mapFieldSchema.isImmutable(mapField)) {
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField, mapField);
            UnsafeUtil.putObject(message, offset, mapField);
        }
        reader.readMap(this.mapFieldSchema.forMutableMapData(mapField), this.mapFieldSchema.forMapMetadata(mapDefaultEntry), extensionRegistry);
    }

    private <UT, UB> UB filterMapUnknownEnumValues(Object obj, int i, UB ub, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object obj2) {
        Internal.EnumVerifier enumFieldVerifier;
        int iNumberAt = numberAt(i);
        Object object = UnsafeUtil.getObject(obj, offset(typeAndOffsetAt(i)));
        if (object == null || (enumFieldVerifier = getEnumFieldVerifier(i)) == null) {
            return ub;
        }
        return (UB) filterUnknownEnumMap(i, iNumberAt, this.mapFieldSchema.forMutableMapData(object), enumFieldVerifier, ub, unknownFieldSchema, obj2);
    }

    private <K, V, UT, UB> UB filterUnknownEnumMap(int i, int i2, Map<K, V> map, Internal.EnumVerifier enumVerifier, UB ub, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object obj) {
        MapEntryLite.Metadata<?, ?> metadataForMapMetadata = this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(i));
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> next = it.next();
            if (!enumVerifier.isInRange(((Integer) next.getValue()).intValue())) {
                if (ub == null) {
                    ub = unknownFieldSchema.getBuilderFromMessage(obj);
                }
                ByteString.CodedBuilder codedBuilderNewCodedBuilder = ByteString.newCodedBuilder(MapEntryLite.computeSerializedSize(metadataForMapMetadata, next.getKey(), next.getValue()));
                try {
                    MapEntryLite.writeTo(codedBuilderNewCodedBuilder.getCodedOutput(), metadataForMapMetadata, next.getKey(), next.getValue());
                    unknownFieldSchema.addLengthDelimited(ub, i2, codedBuilderNewCodedBuilder.build());
                    it.remove();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ub;
    }

    @Override // com.google.protobuf.Schema
    public final boolean isInitialized(T message) {
        int currentPresenceField;
        int pos;
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceFieldOffset2 = 0;
        int i = 0;
        while (i < this.checkInitializedCount) {
            int pos2 = this.intArray[i];
            int number = numberAt(pos2);
            int typeAndOffset = typeAndOffsetAt(pos2);
            int presenceMaskAndOffset = this.buffer[pos2 + 2];
            int presenceFieldOffset = presenceMaskAndOffset & 1048575;
            int presenceMask = 1 << (presenceMaskAndOffset >>> OFFSET_BITS);
            if (presenceFieldOffset == currentPresenceFieldOffset) {
                int i2 = currentPresenceFieldOffset2;
                currentPresenceField = currentPresenceFieldOffset;
                pos = i2;
            } else if (presenceFieldOffset == 1048575) {
                int i3 = currentPresenceFieldOffset2;
                currentPresenceField = presenceFieldOffset;
                pos = i3;
            } else {
                int currentPresenceField2 = UNSAFE.getInt(message, presenceFieldOffset);
                currentPresenceField = presenceFieldOffset;
                pos = currentPresenceField2;
            }
            if (isRequired(typeAndOffset) && !isFieldPresent(message, pos2, currentPresenceField, pos, presenceMask)) {
                return false;
            }
            switch (type(typeAndOffset)) {
                case 9:
                case 17:
                    if (isFieldPresent(message, pos2, currentPresenceField, pos, presenceMask) && !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos2))) {
                        return false;
                    }
                    break;
                    break;
                case 27:
                case 49:
                    if (!isListInitialized(message, typeAndOffset, pos2)) {
                        return false;
                    }
                    break;
                    break;
                case 50:
                    if (!isMapInitialized(message, typeAndOffset, pos2)) {
                        return false;
                    }
                    break;
                    break;
                case 60:
                case 68:
                    if (isOneofPresent(message, number, pos2) && !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos2))) {
                        return false;
                    }
                    break;
                    break;
            }
            i++;
            currentPresenceFieldOffset = currentPresenceField;
            currentPresenceFieldOffset2 = pos;
        }
        return !this.hasExtensions || this.extensionSchema.getExtensions(message).isInitialized();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean isInitialized(Object message, int typeAndOffset, Schema schema) {
        Object nested = UnsafeUtil.getObject(message, offset(typeAndOffset));
        return schema.isInitialized(nested);
    }

    private <N> boolean isListInitialized(Object message, int typeAndOffset, int pos) {
        List<N> list = (List) UnsafeUtil.getObject(message, offset(typeAndOffset));
        if (list.isEmpty()) {
            return true;
        }
        Schema schema = getMessageFieldSchema(pos);
        for (int i = 0; i < list.size(); i++) {
            N nested = list.get(i);
            if (!schema.isInitialized(nested)) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4, types: [com.google.protobuf.Schema] */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    private boolean isMapInitialized(T t, int i, int i2) {
        Map<?, ?> mapForMapData = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(t, offset(i)));
        if (mapForMapData.isEmpty()) {
            return true;
        }
        if (this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(i2)).valueType.getJavaType() != WireFormat.JavaType.MESSAGE) {
            return true;
        }
        ?? SchemaFor = 0;
        for (Object obj : mapForMapData.values()) {
            if (SchemaFor == 0) {
                SchemaFor = SchemaFor;
                SchemaFor = Protobuf.getInstance().schemaFor((Class) obj.getClass());
            }
            SchemaFor = SchemaFor;
            if (!SchemaFor.isInitialized(obj)) {
                return false;
            }
        }
        return true;
    }

    private void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
        if (value instanceof String) {
            writer.writeString(fieldNumber, (String) value);
        } else {
            writer.writeBytes(fieldNumber, (ByteString) value);
        }
    }

    private void readString(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (isEnforceUtf8(typeAndOffset)) {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readStringRequireUtf8());
        } else if (this.lite) {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readString());
        } else {
            UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
        }
    }

    private void readStringList(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (isEnforceUtf8(typeAndOffset)) {
            reader.readStringListRequireUtf8(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
        } else {
            reader.readStringList(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
        }
    }

    private <E> void readMessageList(Object message, int typeAndOffset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        long offset = offset(typeAndOffset);
        reader.readMessageList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private <E> void readGroupList(Object message, long offset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        reader.readGroupList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private int numberAt(int pos) {
        return this.buffer[pos];
    }

    private int typeAndOffsetAt(int pos) {
        return this.buffer[pos + 1];
    }

    private int presenceMaskAndOffsetAt(int pos) {
        return this.buffer[pos + 2];
    }

    private static int type(int value) {
        return (FIELD_TYPE_MASK & value) >>> OFFSET_BITS;
    }

    private static boolean isRequired(int value) {
        return (REQUIRED_MASK & value) != 0;
    }

    private static boolean isEnforceUtf8(int value) {
        return (ENFORCE_UTF8_MASK & value) != 0;
    }

    private static boolean isLegacyEnumIsClosed(int value) {
        return (LEGACY_ENUM_IS_CLOSED_MASK & value) != 0;
    }

    private static long offset(int value) {
        return 1048575 & value;
    }

    private static boolean isMutable(Object message) {
        if (message == null) {
            return false;
        }
        if (message instanceof GeneratedMessageLite) {
            return ((GeneratedMessageLite) message).isMutable();
        }
        return true;
    }

    private static void checkMutable(Object message) {
        if (!isMutable(message)) {
            throw new IllegalArgumentException("Mutating immutable message: " + message);
        }
    }

    private static <T> double doubleAt(T message, long offset) {
        return UnsafeUtil.getDouble(message, offset);
    }

    private static <T> float floatAt(T message, long offset) {
        return UnsafeUtil.getFloat(message, offset);
    }

    private static <T> int intAt(T message, long offset) {
        return UnsafeUtil.getInt(message, offset);
    }

    private static <T> long longAt(T message, long offset) {
        return UnsafeUtil.getLong(message, offset);
    }

    private static <T> boolean booleanAt(T message, long offset) {
        return UnsafeUtil.getBoolean(message, offset);
    }

    private static <T> double oneofDoubleAt(T message, long offset) {
        return ((Double) UnsafeUtil.getObject(message, offset)).doubleValue();
    }

    private static <T> float oneofFloatAt(T message, long offset) {
        return ((Float) UnsafeUtil.getObject(message, offset)).floatValue();
    }

    private static <T> int oneofIntAt(T message, long offset) {
        return ((Integer) UnsafeUtil.getObject(message, offset)).intValue();
    }

    private static <T> long oneofLongAt(T message, long offset) {
        return ((Long) UnsafeUtil.getObject(message, offset)).longValue();
    }

    private static <T> boolean oneofBooleanAt(T message, long offset) {
        return ((Boolean) UnsafeUtil.getObject(message, offset)).booleanValue();
    }

    private boolean arePresentForEquals(T message, T other, int pos) {
        return isFieldPresent(message, pos) == isFieldPresent(other, pos);
    }

    private boolean isFieldPresent(T message, int pos, int presenceFieldOffset, int presenceField, int presenceMask) {
        if (presenceFieldOffset == 1048575) {
            return isFieldPresent(message, pos);
        }
        return (presenceField & presenceMask) != 0;
    }

    private boolean isFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = presenceMaskAndOffset & 1048575;
        if (presenceFieldOffset == 1048575) {
            int typeAndOffset = typeAndOffsetAt(pos);
            long offset = offset(typeAndOffset);
            switch (type(typeAndOffset)) {
                case 0:
                    return Double.doubleToRawLongBits(UnsafeUtil.getDouble(message, offset)) != 0;
                case 1:
                    return Float.floatToRawIntBits(UnsafeUtil.getFloat(message, offset)) != 0;
                case 2:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 3:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 4:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 6:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 7:
                    return UnsafeUtil.getBoolean(message, offset);
                case 8:
                    Object value = UnsafeUtil.getObject(message, offset);
                    if (value instanceof String) {
                        return !((String) value).isEmpty();
                    }
                    if (value instanceof ByteString) {
                        return !ByteString.EMPTY.equals(value);
                    }
                    throw new IllegalArgumentException();
                case 9:
                    return UnsafeUtil.getObject(message, offset) != null;
                case 10:
                    return !ByteString.EMPTY.equals(UnsafeUtil.getObject(message, offset));
                case 11:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 12:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 13:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 14:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 15:
                    return UnsafeUtil.getInt(message, offset) != 0;
                case 16:
                    return UnsafeUtil.getLong(message, offset) != 0;
                case 17:
                    return UnsafeUtil.getObject(message, offset) != null;
                default:
                    throw new IllegalArgumentException();
            }
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> OFFSET_BITS);
        return (UnsafeUtil.getInt(message, (long) (1048575 & presenceMaskAndOffset)) & presenceMask) != 0;
    }

    private void setFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = 1048575 & presenceMaskAndOffset;
        if (presenceFieldOffset == 1048575) {
            return;
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> OFFSET_BITS);
        UnsafeUtil.putInt(message, presenceFieldOffset, UnsafeUtil.getInt(message, presenceFieldOffset) | presenceMask);
    }

    private boolean isOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long) (1048575 & presenceMaskAndOffset)) == fieldNumber;
    }

    private boolean isOneofCaseEqual(T message, T other, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long) (presenceMaskAndOffset & 1048575)) == UnsafeUtil.getInt(other, (long) (1048575 & presenceMaskAndOffset));
    }

    private void setOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
        UnsafeUtil.putInt(message, 1048575 & presenceMaskAndOffset, fieldNumber);
    }

    private int positionForFieldNumber(int number) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return slowPositionForFieldNumber(number, 0);
        }
        return -1;
    }

    private int positionForFieldNumber(int number, int min) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return slowPositionForFieldNumber(number, min);
        }
        return -1;
    }

    private int slowPositionForFieldNumber(int number, int min) {
        int max = (this.buffer.length / 3) - 1;
        while (min <= max) {
            int mid = (max + min) >>> 1;
            int pos = mid * 3;
            int midFieldNumber = numberAt(pos);
            if (number == midFieldNumber) {
                return pos;
            }
            if (number < midFieldNumber) {
                max = mid - 1;
            } else {
                min = mid + 1;
            }
        }
        return -1;
    }

    int getSchemaSize() {
        return this.buffer.length * 3;
    }
}
