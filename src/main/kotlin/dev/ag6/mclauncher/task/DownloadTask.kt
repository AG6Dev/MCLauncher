package dev.ag6.mclauncher.task

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.util.verifyFileSHA1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream

open class DownloadTask(
    name: String,
    private val url: String,
    private val destination: Path,
    private val sha1Hash: String? = null
) : Task<Path>(name) {
    override suspend fun run(): Path = withContext(Dispatchers.IO) {
        destination.createParentDirectories()

        val request = Request.Builder().get().url(url).build()
        MCLauncher.HTTP_CLIENT.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Failed to download file from $url: ${response.code}")
            }

            val body = response.body
            val size = body.contentLength()

            body.byteStream().use { stream ->
                destination.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0

                    while (stream.read(buffer).also { bytesRead = it } != -1) {
                        currentCoroutineContext().ensureActive()

                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        if (size > 0) {
                            progress = totalBytesRead.toFloat() / size
                        }
                    }
                }
            }
        }

        sha1Hash?.let {
            if (!verifyFileSHA1(destination, it)) {
                destination.deleteIfExists()
                throw IllegalStateException("SHA-1 hash mismatch for downloaded file from $url, deleted file.")
            }
        }
        progress = 1.0f
        destination
    }
}