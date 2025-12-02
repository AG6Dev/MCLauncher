package dev.ag6.mclauncher.util

import dev.ag6.mclauncher.MCLauncher
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.inputStream

fun getDefaultDataLocation(): Path {
    val userHome = System.getProperty("user.home")

    return when (OperatingSystem.CURRENT_OS) {
        OperatingSystem.WINDOWS -> Paths.get(System.getenv("APPDATA"), "MCLauncher")
        OperatingSystem.MACOS -> Paths.get(userHome, "Library", "Application Support", "MCLauncher")
        else -> Paths.get(userHome, ".mclauncher")
    }
}

fun verifyFileSHA1(filePath: Path, expectedHash: String): Boolean {
    val digest = MessageDigest.getInstance("SHA-1")
    filePath.inputStream().use { inputStream ->
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }

    val fileHash = digest.digest().joinToString("") { "%02x".format(it) }
    return fileHash.equals(expectedHash, ignoreCase = true)
}

fun getResourceStream(path: String): InputStream? {
    return MCLauncher::class.java.classLoader.getResourceAsStream(path)
}

fun getResource(path: String): URL? {
    return MCLauncher::class.java.classLoader.getResource(path)
}

fun String.toPath(): Path = Path.of(this)