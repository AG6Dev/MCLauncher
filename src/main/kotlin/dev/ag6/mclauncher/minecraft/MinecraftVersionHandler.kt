package dev.ag6.mclauncher.minecraft

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import okhttp3.Request

object MinecraftVersionHandler {
    private const val VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

    val minecraftVersions: ObservableList<MinecraftVersion> = FXCollections.observableArrayList()
    lateinit var latestVersion: MinecraftVersion
    lateinit var latestSnapshot: MinecraftVersion

    fun fetchGameVersions() {
        try {
            val request = Request.Builder().url(VERSION_MANIFEST_URL).get().build()

            val response = MCLauncher.HTTP_CLIENT.newCall(request).execute()
            response.use {
                if (!response.isSuccessful) {
                    error("Failed to fetch version manifest. Status code: ${response.code}")
                }

                val body = response.body.string()

                val jsonObject = MCLauncher.GSON.fromJson(body, JsonObject::class.java)
                val versions = jsonObject.getAsJsonArray("versions")

                for (jsonElement in versions) {
                    minecraftVersions.add(MCLauncher.GSON.fromJson(jsonElement, MinecraftVersion::class.java))
                }

                val latest = jsonObject.getAsJsonObject("latest")
                val latestReleaseId = latest.asJsonObject.get("release").asString
                val latestSnapshotId = latest.asJsonObject.get("snapshot").asString

                latestVersion =
                    minecraftVersions.find { it.id == latestReleaseId } ?: error("Latest release version not found.")
                latestSnapshot =
                    minecraftVersions.find { it.id == latestSnapshotId } ?: error("Latest snapshot version not found.")

                MCLauncher.LOGGER.info { "Fetched ${minecraftVersions.size} game versions." }
            }
        } catch (e: Exception) {
            MCLauncher.LOGGER.error(e) { "Error fetching game versions: ${e.message}" }
        }
    }

    fun getVersion(id: String): MinecraftVersion? {
        return minecraftVersions.find { it.id == id }
    }
}

