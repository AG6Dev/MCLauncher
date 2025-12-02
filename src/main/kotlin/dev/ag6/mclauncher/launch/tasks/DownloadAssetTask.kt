package dev.ag6.mclauncher.launch.tasks

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.minecraft.asset.AssetInfo
import dev.ag6.mclauncher.task.DownloadTask
import dev.ag6.mclauncher.task.Task
import dev.ag6.mclauncher.util.verifyFileSHA1
import java.nio.file.Path
import kotlin.io.path.exists

class DownloadAssetTask(private val assetInfo: AssetInfo) : Task<Path>("Download Asset") {
    override suspend fun run(): Path {
        val assetPath = assetInfo.getAssetPath()
        val fullPath = InstanceLauncher.ASSETS_LOCATION.resolve("objects").resolve(assetPath)

        if (fullPath.exists()) {
            if (verifyFileSHA1(fullPath, assetInfo.hash)) {
                return fullPath
            }
            MCLauncher.LOGGER.warn { "Asset file $fullPath is corrupted, re-downloading..." }
        }

        val downloadTask = DownloadTask("Download Asset", (MINECRAFT_ASSET_URL + assetPath), fullPath, assetInfo.hash)
        return downloadTask.execute()
    }

    companion object {
        private const val MINECRAFT_ASSET_URL = "https://resources.download.minecraft.net/"
    }
}