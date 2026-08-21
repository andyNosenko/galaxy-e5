package reconstructed.fastdeploy

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

/**
 * Clean high-level reconstruction of the Android-side fastdeploy agent found in
 * `installer-starshine-1.8.0.exe`.
 *
 * This is not the original source file. It is a readable reconstruction based on
 * the decompiled `com.android.fastdeploy.DeployAgent` classes extracted from the binary.
 *
 * What this agent actually does:
 * 1. `dump VERSION PACKAGE`
 *    - resolves the installed APK path using `pm list packages -f`
 *    - reads APK central-directory/signature metadata
 *    - emits a protobuf-like payload to stdout
 *
 * 2. `apply PATCHFILE -o [FILE|-]`
 *    - applies a FASTDEPLOY patch
 *    - writes the rebuilt APK bytes to a file or stdout
 *
 * 3. `apply PATCHFILE -pm [session args...]`
 *    - creates a package-manager install session
 *    - streams the rebuilt APK into `pm install-write`
 *    - commits the session with `pm install-commit`
 */
object ReconstructedDeployAgent {
    private const val AGENT_VERSION = 3
    private const val BUFFER_SIZE = 128 * 1024
    private const val PATCH_SIGNATURE = "FASTDEPLOY"

    @JvmStatic
    fun main(args: Array<String>) {
        val exitCode = try {
            run(args)
        } catch (t: Throwable) {
            System.err.println("Error: $t")
            t.printStackTrace()
            2
        }

        kotlin.system.exitProcess(exitCode)
    }

    private fun run(args: Array<String>): Int {
        if (args.isEmpty()) {
            showUsage(0)
        }

        return when (args[0]) {
            "dump" -> handleDump(args)
            "apply" -> handleApply(args)
            else -> showUsage(1)
        }
    }

    private fun handleDump(args: Array<String>): Int {
        if (args.size != 3) {
            return showUsage(1)
        }

        val requiredVersion = args[1].toInt()
        if (requiredVersion != AGENT_VERSION) {
            // Original code printed agent version in hex when versions differ.
            System.out.printf("0x%08X\n", AGENT_VERSION)
            return 4
        }

        val packageName = args[2]
        val packagePath = resolveInstalledApkPath(packageName) ?: return 3

        dumpApkMetadata(packageName, packagePath)
        return 0
    }

    private fun handleApply(args: Array<String>): Int {
        if (args.size < 3) {
            return showUsage(1)
        }

        val patchInput = if (args[1] == "-") {
            System.`in`
        } else {
            FileInputStream(args[1])
        }

        patchInput.use { input ->
            return when (args[2]) {
                "-o" -> {
                    val output = when {
                        args.size > 3 && args[3] != "-" -> FileOutputStream(args[3])
                        else -> System.out
                    }

                    output.use { out ->
                        applyPatchToOutput(input, out)
                    }
                    0
                }

                "-pm" -> {
                    val sessionArgs = if (args.size > 3) args.copyOfRange(3, args.size) else emptyArray()
                    applyPatchToPackageManager(input, sessionArgs)
                }

                else -> showUsage(1)
            }
        }
    }

    private fun showUsage(exitCode: Int): Int {
        System.err.println(
            """
            usage: deployagent <command> [<args>]

            commands:
            dump VERSION PKGNAME
                dump info for an installed package when VERSION matches the current agent version

            apply PATCHFILE [-o|-pm]
                apply a patch from PATCHFILE ("-" means stdin)

              -o <FILE>
                write rebuilt APK bytes to FILE, or stdout when FILE is "-" / omitted

              -pm <ARGS>
                stream rebuilt APK bytes into package manager install session
                and forward <ARGS> to `pm install-create`
            """.trimIndent()
        )
        return exitCode
    }

    /**
     * Exact command pattern recovered from the decompiled code:
     *   pm list packages -f <packageName>
     */
    private fun resolveInstalledApkPath(packageName: String): String? {
        val process = executeAndWait("pm list packages -f $packageName")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val suffix = "=$packageName"

        while (true) {
            val line = reader.readLine() ?: return null
            if (!line.endsWith(suffix)) {
                continue
            }

            val packagePrefixIndex = line.indexOf("package:")
            if (packagePrefixIndex == -1) {
                throw IOException("error reading package list")
            }

            val equalsIndex = line.lastIndexOf(suffix)
            return line.substring(packagePrefixIndex + "package:".length, equalsIndex)
        }
    }

    private fun dumpApkMetadata(packageName: String, apkPath: String) {
        val dump = ReconstructedApkArchive(File(apkPath)).extractMetadata()

        // Original binary serialized a protobuf APKDump message to stdout.
        // Here we keep the same logical payload, but expose it as a simple byte blob.
        val payload = ByteArrayOutputStream().apply {
            writeUtf8Line("name=$packageName")
            writeUtf8Line("absolutePath=$apkPath")
            dump.centralDirectory?.let { writeSizedBlob("cd", it) }
            dump.signatureBlock?.let { writeSizedBlob("signature", it) }
        }

        payload.writeTo(System.out)
    }

    /**
     * Exact command pattern recovered from the decompiled code:
     *   pm install-create <sessionArgs...>
     */
    private fun createInstallSession(sessionArgs: Array<String>): Int {
        val command = buildString {
            append("pm install-create ")
            sessionArgs.forEach {
                append(it)
                append(' ')
            }
        }

        val process = executeAndWait(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val prefix = "Success: created install session ["

        while (true) {
            val line = reader.readLine() ?: return -1
            if (line.startsWith(prefix) && line.endsWith("]")) {
                return line.substring(prefix.length, line.lastIndexOf(']')).toInt()
            }
        }
    }

    /**
     * Exact command pattern recovered from the decompiled code:
     *   pm install-commit <sessionId> -- -
     */
    private fun commitInstallSession(sessionId: Int): Int {
        val process = executeAndWait("pm install-commit $sessionId -- - ")
        return process.exitValue()
    }

    private fun applyPatchToPackageManager(patchInput: InputStream, sessionArgs: Array<String>): Int {
        val sessionId = createInstallSession(sessionArgs)
        if (sessionId < 0) {
            System.err.println("PM Create Session Failed")
            return -1
        }

        val writeExitCode = writePatchedDataToSession(patchInput, sessionId)
        if (writeExitCode != 0) {
            return -1
        }

        return commitInstallSession(sessionId)
    }

    private fun applyPatchToOutput(patchInput: InputStream, output: OutputStream): Long {
        val header = readPatchHeader(patchInput)
        val bytesWritten = writePatchedData(header.newSize, header.baseApkPath, patchInput, output)
        output.flush()

        if (bytesWritten != header.newSize) {
            throw PatchFormatException(
                "output size mismatch (expected ${header.newSize} but wrote $bytesWritten)"
            )
        }

        return bytesWritten
    }

    private fun readPatchHeader(input: InputStream): PatchHeader {
        val signatureBytes = ByteArray(PATCH_SIGNATURE.length)

        try {
            readFully(input, signatureBytes)
            val signature = String(signatureBytes)
            if (signature != PATCH_SIGNATURE) {
                throw PatchFormatException("bad signature")
            }

            val newSize = readLittleEndianLong(input)
            if (newSize < 0) {
                throw PatchFormatException("bad newSize: $newSize")
            }

            val baseApkPath = readSizedString(input)
            return PatchHeader(newSize, baseApkPath)
        } catch (_: IOException) {
            throw PatchFormatException("truncated signature")
        }
    }

    /**
     * Recovered patch format:
     * - "FASTDEPLOY" signature
     * - little-endian newSize
     * - little-endian string length + base APK path
     * - repeated chunks:
     *   1) newDataLen
     *   2) literal new bytes
     *   3) oldDataOffset
     *   4) oldDataLen
     *
     * Result stream = [literal bytes] + [copied bytes from base APK ranges]
     */
    private fun writePatchedData(
        expectedNewSize: Long,
        baseApkPath: String,
        patchInput: InputStream,
        output: OutputStream
    ): Long {
        RandomAccessFile(baseApkPath, "r").use { file ->
            val oldData = file.channel
            val newDataChannel: WritableByteChannel = Channels.newChannel(output)
            val buffer = ByteArray(BUFFER_SIZE)
            var written = 0L

            while (written < expectedNewSize) {
                val newDataLen = readLittleEndianLong(patchInput)
                if (newDataLen > 0) {
                    pipeExactly(patchInput, output, buffer, newDataLen)
                }

                val oldDataOffset = readLittleEndianLong(patchInput)
                val oldDataLen = readLittleEndianLong(patchInput)

                if (oldDataLen >= 0) {
                    var remaining = oldDataLen
                    var offset = oldDataOffset

                    while (remaining > 0) {
                        val chunkLen = minOf(remaining, 1_073_741_824L)
                        oldData.transferTo(offset, chunkLen, newDataChannel)
                        remaining -= chunkLen
                        offset += chunkLen
                    }
                }

                written += newDataLen + oldDataLen
            }

            return written
        }
    }

    /**
     * Exact command pattern recovered from the decompiled code:
     *   pm install-write -S <newSize> <sessionId> -- -
     */
    private fun writePatchedDataToSession(patchInput: InputStream, sessionId: Int): Int {
        val header = readPatchHeader(patchInput)
        val process = Runtime.getRuntime().exec(
            "pm install-write -S ${header.newSize} $sessionId -- -"
        )

        process.outputStream.use { sessionOutput ->
            val written = writePatchedData(header.newSize, header.baseApkPath, patchInput, sessionOutput)
            sessionOutput.flush()

            process.waitFor()

            if (written != header.newSize) {
                throw PatchFormatException(
                    "output size mismatch (expected ${header.newSize} but wrote $written)"
                )
            }
        }

        return process.exitValue()
    }

    private fun executeAndWait(command: String): Process {
        try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            return process
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("interrupted while executing command: $command", e)
        }
    }

    private fun readLittleEndianLong(input: InputStream): Long {
        val buffer = ByteArray(8)
        readFully(input, buffer)
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).long
    }

    private fun readSizedString(input: InputStream): String {
        val size = readLittleEndianLong(input).toInt()
        val buffer = ByteArray(size)
        readFully(input, buffer)
        return String(buffer)
    }

    private fun readFully(input: InputStream, destination: ByteArray, startAt: Int = 0, numBytes: Int = destination.size) {
        var totalRead = 0
        while (totalRead < numBytes) {
            val readNow = input.read(destination, startAt + totalRead, numBytes - totalRead)
            if (readNow == -1) {
                throw IOException("truncated input stream")
            }
            totalRead += readNow
        }
    }

    private fun pipeExactly(input: InputStream, output: OutputStream, buffer: ByteArray, copyLength: Long) {
        var remaining = copyLength
        while (remaining > 0) {
            val step = minOf(buffer.size.toLong(), remaining).toInt()
            readFully(input, buffer, 0, step)
            output.write(buffer, 0, step)
            remaining -= step
        }
    }

    private fun ByteArrayOutputStream.writeUtf8Line(value: String) {
        write(value.toByteArray())
        write('\n'.code)
    }

    private fun ByteArrayOutputStream.writeSizedBlob(name: String, value: ByteArray) {
        writeUtf8Line("$name.size=${value.size}")
        write(value)
        write('\n'.code)
    }

    data class PatchHeader(
        val newSize: Long,
        val baseApkPath: String
    )

    class PatchFormatException(message: String) : IOException(message)
}

/**
 * Clean reconstruction of the APK metadata reader used by DeployAgent.dump.
 *
 * Purpose:
 * - locate the ZIP End Of Central Directory record
 * - read the Central Directory bytes
 * - optionally read the APK Signature Block v2/v3 area ("APK Sig Block 42")
 */
class ReconstructedApkArchive(private val apk: File) {
    data class Dump(
        val centralDirectory: ByteArray?,
        val signatureBlock: ByteArray?
    )

    private data class Location(val offset: Long, val size: Long)

    fun extractMetadata(): Dump {
        RandomAccessFile(apk, "r").use { file ->
            val channel = file.channel
            val centralDirectoryLocation = findCentralDirectory(channel)
            val centralDirectory = readBytes(channel, centralDirectoryLocation)

            val signatureLocation = findSignatureBlock(channel, centralDirectoryLocation.offset)
            val signature = signatureLocation?.let { readBytes(channel, it) }

            return Dump(centralDirectory = centralDirectory, signatureBlock = signature)
        }
    }

    private fun findCentralDirectory(channel: FileChannel): Location {
        val eocdOffset = findEndOfCentralDirectory(channel)
            ?: throw IllegalArgumentException("Unable to find End of Central Directory record.")

        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, eocdOffset, 22L).order(ByteOrder.LITTLE_ENDIAN)
        val signature = buffer.int
        if (signature != 0x06054B50) {
            throw IllegalArgumentException("Not a Central Directory record.")
        }

        buffer.position(12)
        val size = buffer.int.toLong() and 0xFFFFFFFFL
        val offset = buffer.int.toLong() and 0xFFFFFFFFL
        return Location(offset, size)
    }

    private fun findEndOfCentralDirectory(channel: FileChannel): Long? {
        val fileSize = channel.size()
        val windowSize = minOf(fileSize, 65_557L).toInt()
        val readOffset = fileSize - windowSize
        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, readOffset, windowSize.toLong())
            .order(ByteOrder.LITTLE_ENDIAN)

        var pos = windowSize - 22
        while (pos >= 0) {
            val signature = buffer.getInt(pos)
            if (signature == 0x06054B50) {
                return readOffset + pos
            }
            pos--
        }
        return null
    }

    private fun findSignatureBlock(channel: FileChannel, centralDirectoryOffset: Long): Location? {
        val trailerOffset = centralDirectoryOffset - 24
        if (trailerOffset < 0) {
            return null
        }

        val trailer = channel.map(FileChannel.MapMode.READ_ONLY, trailerOffset, 24L)
            .order(ByteOrder.LITTLE_ENDIAN)

        val size = trailer.long
        val magic = ByteArray(16)
        trailer.get(magic)

        if (String(magic) != "APK Sig Block 42") {
            return null
        }

        val startOffset = centralDirectoryOffset - size - 8
        return Location(startOffset, size)
    }

    private fun readBytes(channel: FileChannel, location: Location): ByteArray {
        val payload = ByteArray(location.size.toInt())
        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, location.offset, location.size)
        buffer.get(payload)
        return payload
    }
}
