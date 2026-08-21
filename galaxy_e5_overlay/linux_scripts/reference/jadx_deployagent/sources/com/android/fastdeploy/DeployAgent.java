package com.android.fastdeploy;

import com.google.protobuf.ByteString;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/* JADX INFO: loaded from: /Users/andrey/work/cuspy/php-grids/_decompile/starshine_1.8.0/extracted/archive_20_off_23686200_len_238170/classes.dex */
public final class DeployAgent {
    private static final int AGENT_VERSION = 3;
    private static final int BUFFER_SIZE = 131072;

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:8:0x0015  */
    public static void main(String[] args) {
        InputStream deltaInputStream;
        int exitCode = 0;
        try {
            if (args.length < 1) {
                showUsage(0);
            }
            String commandString = args[0];
            switch (commandString) {
                case "dump":
                    if (args.length != 3) {
                        showUsage(1);
                    }
                    String requiredVersion = args[1];
                    if (3 == Integer.parseInt(requiredVersion)) {
                        String packageName = args[2];
                        String packagePath = getFilenameFromPackageName(packageName);
                        if (packagePath != null) {
                            dumpApk(packageName, packagePath);
                        } else {
                            exitCode = 3;
                        }
                        break;
                    } else {
                        System.out.printf("0x%08X\n", 3);
                        exitCode = 4;
                        break;
                    }
                    break;
                case "apply":
                    if (args.length < 3) {
                        showUsage(1);
                    }
                    String patchPath = args[1];
                    String outputParam = args[2];
                    if (patchPath.compareTo("-") == 0) {
                        deltaInputStream = System.in;
                    } else {
                        deltaInputStream = new FileInputStream(patchPath);
                    }
                    if (!outputParam.equals("-o")) {
                        if (outputParam.equals("-pm")) {
                            String[] sessionArgs = null;
                            if (args.length > 3) {
                                int numSessionArgs = args.length - 3;
                                sessionArgs = new String[numSessionArgs];
                                for (int i = 0; i < numSessionArgs; i++) {
                                    sessionArgs[i] = args[i + 3];
                                }
                            }
                            exitCode = applyPatch(deltaInputStream, sessionArgs);
                        }
                        break;
                    } else {
                        OutputStream outputStream = null;
                        if (args.length > 3) {
                            String outputPath = args[3];
                            if (!outputPath.equals("-")) {
                                outputStream = new FileOutputStream(outputPath);
                            }
                        }
                        if (outputStream == null) {
                            outputStream = System.out;
                        }
                        writePatchToStream(deltaInputStream, outputStream);
                        break;
                    }
                    break;
                default:
                    showUsage(1);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace();
            System.exit(2);
        }
        System.exit(exitCode);
    }

    private static void showUsage(int exitCode) {
        System.err.println("usage: deployagent <command> [<args>]\n\ncommands:\ndump VERSION PKGNAME  dump info for an installed package given that VERSION equals current agent's version\napply PATCHFILE [-o|-pm]    apply a patch from PATCHFILE (- for stdin) to an installed package\n -o <FILE> directs output to FILE, default or - for stdout\n -pm <ARGS> directs output to package manager, passes <ARGS> to 'pm install-create'\n");
        System.exit(exitCode);
    }

    private static Process executeCommand(String command) throws IOException {
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return p;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFilenameFromPackageName(String packageName) throws IOException {
        String line;
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("pm list packages -f " + packageName);
        Process p = executeCommand(commandBuilder.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String packageSuffix = "=" + packageName;
        do {
            line = reader.readLine();
            if (line == null) {
                return null;
            }
        } while (!line.endsWith(packageSuffix));
        int packageIndex = line.indexOf("package:");
        if (packageIndex == -1) {
            throw new IOException("error reading package list");
        }
        int equalsIndex = line.lastIndexOf(packageSuffix);
        String fileName = line.substring("package:".length() + packageIndex, equalsIndex);
        return fileName;
    }

    private static void dumpApk(String packageName, String packagePath) throws IOException {
        File apk = new File(packagePath);
        ApkArchive.Dump dump = new ApkArchive(apk).extractMetadata();
        APKDump.Builder apkDumpBuilder = APKDump.newBuilder();
        apkDumpBuilder.setName(packageName);
        if (dump.cd != null) {
            apkDumpBuilder.setCd(ByteString.copyFrom(dump.cd));
        }
        if (dump.signature != null) {
            apkDumpBuilder.setSignature(ByteString.copyFrom(dump.signature));
        }
        apkDumpBuilder.setAbsolutePath(apk.getAbsolutePath());
        apkDumpBuilder.build().writeTo(System.out);
    }

    private static int createInstallSession(String[] args) throws IOException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("pm install-create ");
        for (int i = 0; args != null && i < args.length; i++) {
            commandBuilder.append(args[i] + " ");
        }
        Process p = executeCommand(commandBuilder.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                if (line.startsWith("Success: created install session [") && line.endsWith("]")) {
                    return Integer.parseInt(line.substring("Success: created install session [".length(), line.lastIndexOf("]")));
                }
            } else {
                return -1;
            }
        }
    }

    private static int commitInstallSession(int sessionId) throws IOException {
        Process p = executeCommand(String.format("pm install-commit %d -- - ", Integer.valueOf(sessionId)));
        return p.exitValue();
    }

    private static int applyPatch(InputStream deltaStream, String[] sessionArgs) throws PatchFormatException, IOException {
        int sessionId = createInstallSession(sessionArgs);
        if (sessionId < 0) {
            System.err.println("PM Create Session Failed");
            return -1;
        }
        int writeExitCode = writePatchedDataToSession(deltaStream, sessionId);
        if (writeExitCode != 0) {
            return -1;
        }
        return commitInstallSession(sessionId);
    }

    private static long writePatchToStream(InputStream patchData, OutputStream outputStream) throws PatchFormatException, IOException {
        long newSize = readPatchHeader(patchData);
        long bytesWritten = writePatchedDataToStream(newSize, patchData, outputStream);
        outputStream.flush();
        if (bytesWritten != newSize) {
            throw new PatchFormatException(String.format("output size mismatch (expected %d but wrote %d)", Long.valueOf(newSize), Long.valueOf(bytesWritten)));
        }
        return bytesWritten;
    }

    private static long readPatchHeader(InputStream patchData) throws PatchFormatException, IOException {
        byte[] signatureBuffer = new byte[PatchUtils.SIGNATURE.length()];
        try {
            PatchUtils.readFully(patchData, signatureBuffer);
            String signature = new String(signatureBuffer);
            if (!PatchUtils.SIGNATURE.equals(signature)) {
                throw new PatchFormatException("bad signature");
            }
            long newSize = PatchUtils.readLELong(patchData);
            if (newSize < 0) {
                throw new PatchFormatException("bad newSize: " + newSize);
            }
            return newSize;
        } catch (IOException e) {
            throw new PatchFormatException("truncated signature");
        }
    }

    private static long writePatchedDataToStream(long newSize, InputStream patchData, OutputStream outputStream) throws IOException {
        InputStream inputStream;
        String deviceFile;
        RandomAccessFile oldDataFile;
        String deviceFile2 = PatchUtils.readString(patchData);
        RandomAccessFile oldDataFile2 = new RandomAccessFile(deviceFile2, "r");
        FileChannel oldData = oldDataFile2.getChannel();
        WritableByteChannel newData = Channels.newChannel(outputStream);
        byte[] buffer = new byte[BUFFER_SIZE];
        long newDataBytesWritten = 0;
        while (newDataBytesWritten < newSize) {
            long newDataLen = PatchUtils.readLELong(patchData);
            if (newDataLen <= 0) {
                inputStream = patchData;
            } else {
                inputStream = patchData;
                PatchUtils.pipe(inputStream, outputStream, buffer, newDataLen);
            }
            long oldDataOffset = PatchUtils.readLELong(inputStream);
            long oldDataLen = PatchUtils.readLELong(inputStream);
            if (oldDataLen < 0) {
                deviceFile = deviceFile2;
                oldDataFile = oldDataFile2;
            } else {
                long offset = oldDataOffset;
                long len = oldDataLen;
                while (len > 0) {
                    String deviceFile3 = deviceFile2;
                    long chunkLen = Math.min(len, 1073741824L);
                    long offset2 = offset;
                    oldData.transferTo(offset2, chunkLen, newData);
                    len -= chunkLen;
                    offset = offset2 + chunkLen;
                    deviceFile2 = deviceFile3;
                    oldDataFile2 = oldDataFile2;
                }
                deviceFile = deviceFile2;
                oldDataFile = oldDataFile2;
            }
            newDataBytesWritten += newDataLen + oldDataLen;
            deviceFile2 = deviceFile;
            oldDataFile2 = oldDataFile;
        }
        return newDataBytesWritten;
    }

    private static int writePatchedDataToSession(InputStream patchData, int sessionId) throws PatchFormatException, IOException {
        try {
            long newSize = readPatchHeader(patchData);
            String command = String.format("pm install-write -S %d %d -- -", Long.valueOf(newSize), Integer.valueOf(sessionId));
            Process p = Runtime.getRuntime().exec(command);
            OutputStream sessionOutputStream = p.getOutputStream();
            long bytesWritten = writePatchedDataToStream(newSize, patchData, sessionOutputStream);
            sessionOutputStream.flush();
            p.waitFor();
            if (bytesWritten != newSize) {
                throw new PatchFormatException(String.format("output size mismatch (expected %d but wrote %d)", Long.valueOf(newSize), Long.valueOf(bytesWritten)));
            }
            return p.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
