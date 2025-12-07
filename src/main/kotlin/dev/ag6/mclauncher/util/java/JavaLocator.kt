package dev.ag6.mclauncher.util.java

import dev.ag6.mclauncher.util.OperatingSystem
import dev.ag6.mclauncher.util.toPath
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable

data class JavaInstallation(
    val name: String,
    val path: Path,
    val version: String,
    val majorVersion: Int,
    val isJdk: Boolean,
)

object JavaLocator {
    private val os = OperatingSystem.CURRENT_OS

    fun discover(): List<JavaInstallation> {
        val potentialPaths = findJavaHomes()
        return potentialPaths
            .mapNotNull { validateInstallation(it) }
            .distinctBy { it.path }
            .sortedByDescending { it.majorVersion }
    }

    private fun findJavaHomes(): Set<Path> {
        val paths = mutableSetOf<Path>()

        System.getenv("JAVA_HOME")?.let { paths.add(File(it).toPath()) }

        getJavaPathsForOs().forEach { path ->
            if (path.exists() && path.isDirectory()) {
                try {
                    File(path.toUri()).listFiles()?.forEach {
                        if (it.isDirectory) {
                            paths.add(it.toPath())
                        }
                    }
                } catch (e: SecurityException) {
                    // Ignore directories we can't access
                }
            }
        }

        return paths
    }

    private fun getJavaPathsForOs(): List<Path> {
        val homeDir = System.getProperty("user.home")
        return when (os) {
            OperatingSystem.WINDOWS -> listOf(
                "C:\\Program Files\\Java",
                "C:\\Program Files (x86)\\Java",
                "C:\\Program Files\\Eclipse Adoptium",
                "$homeDir\\.jdks"
            ).map { File(it).toPath() }

            OperatingSystem.LINUX -> listOf(
                "/usr/lib/jvm",
                "/usr/java",
                "/opt/java",
                "$homeDir/.jdks"
            ).map { File(it).toPath() }

            OperatingSystem.MACOS -> listOf(
                "/Library/Java/JavaVirtualMachines",
                "$homeDir/Library/Java/JavaVirtualMachines",
                "$homeDir/.jdks"
            ).map { it.toPath() }

            else -> emptyList()
        }
    }

    private fun validateInstallation(path: Path): JavaInstallation? {
        val javaExeName = if (os == OperatingSystem.WINDOWS) "java.exe" else "java"
        val javacExeName = if (os == OperatingSystem.WINDOWS) "javac.exe" else "javac"

        val binDir = path / "bin"
        val javaExe = binDir / javaExeName

        if (!javaExe.exists() || !javaExe.isExecutable()) {
            return null
        }

        val isJdk = (binDir / javacExeName).exists()

        try {
            val proc = ProcessBuilder(javaExe.toString(), "-version").redirectErrorStream(true).start()
            proc.waitFor(5, TimeUnit.SECONDS)
            val output = proc.inputStream.bufferedReader().readText()

            val versionLine = output.lines().firstOrNull { it.contains("version") } ?: return null
            val versionString = versionLine.substringAfter('"').substringBefore('"')

            val majorVersion = if (versionString.startsWith("1.")) {
                versionString.substringAfter(".").substringBefore(".").toIntOrNull()
            } else {
                versionString.substringBefore(".").toIntOrNull()
            }

            return majorVersion?.let {
                JavaInstallation(
                    name = path.fileName.toString(),
                    path = path,
                    version = versionString,
                    majorVersion = it,
                    isJdk = isJdk
                )
            }
        } catch (e: Exception) {
            return null
        }
    }
}
