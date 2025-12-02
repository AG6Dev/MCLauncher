package dev.ag6.mclauncher.util

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.*
import java.io.InputStream

class ProcessStreamReader(private val process: Process) {
    private val scope = CoroutineScope(Dispatchers.Default)
    var lines: ObservableList<String> = FXCollections.observableArrayList()

    init {
        val stdout = scope.launch {
            readStream(process.inputStream, "OUT")
        }

        val stderr = scope.launch {
            readStream(process.errorStream, "ERR")
        }

        scope.launch {
            try {
                val exitCode = process.waitFor()

                stdout.cancel()
                stderr.cancel()

                stdout.join()
                stderr.join()

                lines.add("\nProcess exited with code $exitCode\n")
                shutdown()
            } catch (e: InterruptedException) {
                shutdown()
            }
        }
    }

    private suspend fun readStream(inputStream: InputStream, streamName: String) = withContext(Dispatchers.IO) {
        try {
            inputStream.bufferedReader().use {
                while (isActive) {
                    val line = it.readLine() ?: break
                    lines.add("[$streamName] $line")
                }
            }
        } catch (e: Exception) {
            if (e !is CancellationException) {
                lines.add("Error reading process $streamName: ${e.message}\n")
            }
        }
    }

    fun shutdown() {
        scope.cancel()
    }
}