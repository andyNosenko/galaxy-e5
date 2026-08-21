package com.android.fastdeploy;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public final class ApkArchive {
    private static final int CD_ENTRY_HEADER_SIZE_BYTES = 22;
    private static final int CD_LOCAL_FILE_HEADER_SIZE_OFFSET = 12;
    private static final long EOCD_MAX_SIZE = 65557;
    private static final int EOCD_MIN_SIZE = 22;
    private static final int EOCD_SIGNATURE = 101010256;
    private static final int EOSIGNATURE_SIZE = 24;
    private static final String TAG = "ApkArchive";
    private final FileChannel mChannel;
    private final RandomAccessFile mFile;

    public static final class Dump {
        final byte[] cd;
        final byte[] signature;

        Dump(byte[] cd, byte[] signature) {
            this.cd = cd;
            this.signature = signature;
        }
    }

    static final class Location {
        final long offset;
        final long size;

        public Location(long offset, long size) {
            this.offset = offset;
            this.size = size;
        }
    }

    public ApkArchive(File apk) throws IOException {
        this.mFile = new RandomAccessFile(apk, "r");
        this.mChannel = this.mFile.getChannel();
    }

    public Dump extractMetadata() throws IOException {
        Location cdLoc = getCDLocation();
        byte[] cd = readMetadata(cdLoc);
        byte[] signature = null;
        Location sigLoc = getSignatureLocation(cdLoc.offset);
        if (sigLoc != null) {
            signature = readMetadata(sigLoc);
            long size = ByteBuffer.wrap(signature).order(ByteOrder.LITTLE_ENDIAN).getLong();
            if (sigLoc.size != size) {
                Log.e(TAG, "Mismatching signature sizes: " + sigLoc.size + " != " + size);
                signature = null;
            }
        }
        return new Dump(cd, signature);
    }

    private long findEndOfCDRecord() throws IOException {
        long fileSize = this.mChannel.size();
        int sizeToRead = Math.toIntExact(Math.min(fileSize, EOCD_MAX_SIZE));
        long readOffset = fileSize - ((long) sizeToRead);
        ByteBuffer buffer = this.mChannel.map(FileChannel.MapMode.READ_ONLY, readOffset, sizeToRead).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(sizeToRead - 22);
        while (signature != EOCD_SIGNATURE) {
            if (buffer.position() != 4) {
                buffer.position((buffer.position() - 4) - 1);
            } else {
                return -1L;
            }
        }
        return (((long) buffer.position()) + readOffset) - 4;
    }

    private Location findCDRecord(ByteBuffer buf) {
        if (buf.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
        }
        if (buf.remaining() < 22) {
            throw new IllegalArgumentException("Input too short. Need at least 22 bytes, available: " + buf.remaining() + "bytes.");
        }
        int originalPosition = buf.position();
        int recordSignature = buf.getInt();
        if (recordSignature != EOCD_SIGNATURE) {
            throw new IllegalArgumentException("Not a Central Directory record. Signature: 0x" + Long.toHexString(4294967295L & ((long) recordSignature)));
        }
        buf.position(originalPosition + CD_LOCAL_FILE_HEADER_SIZE_OFFSET);
        long size = ((long) buf.getInt()) & 4294967295L;
        long offset = ((long) buf.getInt()) & 4294967295L;
        return new Location(offset, size);
    }

    Location getCDLocation() throws IOException {
        long eocdRecord = findEndOfCDRecord();
        if (eocdRecord < 0) {
            throw new IllegalArgumentException("Unable to find End of Central Directory record.");
        }
        Location location = findCDRecord(this.mChannel.map(FileChannel.MapMode.READ_ONLY, eocdRecord, 22L).order(ByteOrder.LITTLE_ENDIAN));
        if (location == null) {
            throw new IllegalArgumentException("Unable to find Central Directory File Header.");
        }
        return location;
    }

    Location getSignatureLocation(long cdRecordOffset) throws IOException {
        long signatureOffset = cdRecordOffset - 24;
        if (signatureOffset < 0) {
            Log.e(TAG, "Unable to find Signature.");
            return null;
        }
        ByteBuffer signature = this.mChannel.map(FileChannel.MapMode.READ_ONLY, signatureOffset, 24L).order(ByteOrder.LITTLE_ENDIAN);
        long size = signature.getLong();
        byte[] sign = new byte[16];
        signature.get(sign);
        String signAsString = new String(sign);
        if (!"APK Sig Block 42".equals(signAsString)) {
            Log.e(TAG, "Signature magic does not match: " + signAsString);
            return null;
        }
        long offset = (cdRecordOffset - size) - 8;
        return new Location(offset, size);
    }

    private byte[] readMetadata(Location loc) throws IOException {
        byte[] payload = new byte[(int) loc.size];
        ByteBuffer buffer = this.mChannel.map(FileChannel.MapMode.READ_ONLY, loc.offset, loc.size);
        buffer.get(payload);
        return payload;
    }
}
