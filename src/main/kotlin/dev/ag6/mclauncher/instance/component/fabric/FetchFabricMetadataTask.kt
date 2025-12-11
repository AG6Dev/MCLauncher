package dev.ag6.mclauncher.instance.component.fabric

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.instance.component.fabric.meta.FabricMetadata
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.task.DownloadTask
import dev.ag6.mclauncher.task.Task
import dev.ag6.mclauncher.util.getDefaultDataLocation
import kotlin.io.path.bufferedReader
import kotlin.io.path.div
import kotlin.io.path.exists

class FetchFabricMetadataTask(private val minecraftVersion: MinecraftVersion, private val loaderVersion: String) :
    Task<FabricMetadata>("Fetch Fabric Metadata") {
    override suspend fun run(): FabricMetadata {
        val location = META_CACHE / minecraftVersion.id / "$loaderVersion.json"
        val url = FABRIC_META_URL.format(minecraftVersion.id, loaderVersion)

        if (location.exists()) {
            return MCLauncher.GSON.fromJson(location.bufferedReader(), FabricMetadata::class.java)
        }

        val downloadTask = DownloadTask(name, url, location)
        downloadTask.execute()

        return MCLauncher.GSON.fromJson(location.bufferedReader(), FabricMetadata::class.java)
    }

    companion object {
        const val FABRIC_META_URL = "https://meta.fabricmc.net/v2/versions/loader/%s/%s"
        val META_CACHE = getDefaultDataLocation() / "meta" / "fabric"
    }
}