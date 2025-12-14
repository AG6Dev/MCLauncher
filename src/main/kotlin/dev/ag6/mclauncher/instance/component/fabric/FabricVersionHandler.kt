package dev.ag6.mclauncher.instance.component.fabric

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.instance.component.fabric.meta.FabricLoader
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import okhttp3.Request
import okio.IOException

typealias FabricVersion = FabricLoader

object FabricVersionHandler {
    //mc version - loader version
    private const val LOADER_VERSION_METADATA_URL = "https://meta.fabricmc.net/v2/versions/loader/%s/%s"

    //mc version
    private const val LOADER_VERSIONS_URL: String = "https://meta.fabricmc.net/v2/versions/loader/%s"

    private val versions: MutableMap<MinecraftVersion, MutableList<FabricVersion>> = mutableMapOf()

    fun fetchForMinecraftVersion(mc: MinecraftVersion): List<FabricVersion> {
        if (versions.containsKey(mc)) return versions[mc]!!

        val url = LOADER_VERSIONS_URL.format(mc.id)
        val request = Request.Builder().url(url).get().build()
        val response = MCLauncher.HTTP_CLIENT.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Failed to fetch Fabric Loader versions for Minecraft ${mc.id}: ${response.code}")
        }

        val body = response.body.string()
        val fabricVersions: List<FabricVersion> =
            MCLauncher.GSON.fromJson(body, Array<FabricVersion>::class.java).toList()
        versions[mc] = fabricVersions.toMutableList()

        return versions[mc]?.toList() ?: emptyList()
    }

    fun fetchSpecificVersion(mc: MinecraftVersion, fabric: String): FabricVersion {
        val url = LOADER_VERSION_METADATA_URL.format(mc.id, fabric)
        val request = Request.Builder().url(url).get().build()
        val response = MCLauncher.HTTP_CLIENT.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Failed to fetch Fabric Loader version $fabric for Minecraft ${mc.id}: ${response.code}")
        }

        val body = response.body.string()
        return MCLauncher.GSON.fromJson(body, FabricVersion::class.java)
    }
}
