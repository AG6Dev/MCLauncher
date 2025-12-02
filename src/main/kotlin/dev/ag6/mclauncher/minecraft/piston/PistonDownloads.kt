package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject

data class PistonDownloads(
    val client: Download,
    val server: Download,
    val clientMappings: Download,
    val serverMappings: Download
) {
    companion object {
        fun fromJson(json: JsonObject): PistonDownloads {
            return PistonDownloads(
                Download.fromJson(json.getAsJsonObject("client")),
                Download.fromJson(json.getAsJsonObject("server")),
                Download.fromJson(json.getAsJsonObject("client_mappings")),
                Download.fromJson(json.getAsJsonObject("server_mappings"))
            )
        }
    }
}
