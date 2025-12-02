package dev.ag6.mclauncher.launch.tasks

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import dev.ag6.mclauncher.task.Task
import dev.ag6.mclauncher.util.getDefaultDataLocation
import okhttp3.Request
import java.nio.file.Path
import kotlin.io.path.*

class FetchVersionMetadataTask(private val minecraftVersion: MinecraftVersion) :
    Task<PistonVersionMetadata>("Get Version Metadata") {
    override suspend fun run(): PistonVersionMetadata {
        val metadataLocation = METADATA_CACHE_LOCATION.resolve(minecraftVersion.id + ".json")

        if (metadataLocation.exists()) {
            try {
                val metadata = metadataLocation.bufferedReader().use {
                    val jsonObject = MCLauncher.GSON.fromJson(it, JsonObject::class.java)
                    PistonVersionMetadata.fromJson(jsonObject)
                }

                if (metadata.id == minecraftVersion.id) {
                    MCLauncher.LOGGER.info { "Using cached metadata for version ${minecraftVersion.id}" }
                    return metadata
                }
                MCLauncher.LOGGER.warn { "Cached metadata version mismatch: ${metadata.id} != ${minecraftVersion.id}" }
            } catch (e: Exception) {
                metadataLocation.deleteIfExists()
                MCLauncher.LOGGER.warn(e) { "Failed to read cached metadata for version ${minecraftVersion.id}, re-downloading" }
            }
        }

        val request = Request.Builder().url(minecraftVersion.url).get().build()
        val response = MCLauncher.HTTP_CLIENT.newCall(request).execute()

        response.use {
            if (!response.isSuccessful) {
                throw Exception("Failed to download version metadata for version ${minecraftVersion.id}: ${response.code} ${response.message}")
            }

            val body = response.body.string()
            val json = MCLauncher.GSON.fromJson(body, JsonObject::class.java)
            val metadata = PistonVersionMetadata.fromJson(json)

            METADATA_CACHE_LOCATION.createDirectories()
            metadataLocation.bufferedWriter().use { writer ->
                MCLauncher.GSON.toJson(json, writer)
            }

            return metadata
        }
    }

    companion object {
        private val METADATA_CACHE_LOCATION: Path = getDefaultDataLocation().resolve("version_meta")
    }
}