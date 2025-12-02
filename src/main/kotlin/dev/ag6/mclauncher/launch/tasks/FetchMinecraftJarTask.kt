package dev.ag6.mclauncher.launch.tasks

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import dev.ag6.mclauncher.task.DownloadTask
import dev.ag6.mclauncher.task.Task
import dev.ag6.mclauncher.util.getDefaultDataLocation
import dev.ag6.mclauncher.util.verifyFileSHA1
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class FetchMinecraftJarTask(private val version: PistonVersionMetadata) : Task<Path>("Fetch ${version.id} Jar") {
    override suspend fun run(): Path {
        val clientPath = CLIENTS_LOCATION.resolve("${version.id}.jar")
        val download = version.downloads.client

        if (clientPath.exists()) {
            if (verifyFileSHA1(clientPath, download.sha1)) {
                return clientPath
            }

            clientPath.deleteIfExists()
            MCLauncher.LOGGER.warn { "Existing Minecraft jar for version ${version.id} has invalid hash, re-downloading." }
        }

        val downloadTask = DownloadTask(name, download.url, clientPath, download.sha1)
        downloadTask.execute()

        return clientPath
    }

    companion object {
        private val CLIENTS_LOCATION: Path = getDefaultDataLocation().resolve("client")
    }
}
