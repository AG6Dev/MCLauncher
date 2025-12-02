package dev.ag6.mclauncher.launch.tasks

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.minecraft.asset.AssetIndex
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import dev.ag6.mclauncher.task.DownloadTask
import dev.ag6.mclauncher.task.Task
import dev.ag6.mclauncher.util.verifyFileSHA1
import kotlin.io.path.bufferedReader
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class FetchAssetIndexTask(private val version: PistonVersionMetadata) : Task<AssetIndex>("Get Asset Index") {
    override suspend fun run(): AssetIndex {
        val index = version.assetIndex
        val indexPath = InstanceLauncher.ASSETS_LOCATION.resolve("indexes").resolve("${index.id}.json")

        if (indexPath.exists()) {
            if (verifyFileSHA1(indexPath, index.sha1)) {
                indexPath.bufferedReader().use {
                    val json = MCLauncher.GSON.fromJson(it, JsonObject::class.java)
                    return AssetIndex.fromJson(index.id, json)
                }
            }
            indexPath.deleteIfExists()
            MCLauncher.LOGGER.warn { "Existing asset index for version ${version.id} has invalid hash, re-downloading." }
        }

        val downloadTask = DownloadTask("Download Asset Index", index.url, indexPath, index.sha1)
        downloadTask.execute()

        indexPath.bufferedReader().use {
            val json = MCLauncher.GSON.fromJson(it, JsonObject::class.java)
            return AssetIndex.fromJson(index.id, json)
        }
    }
}