package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher

data class PistonLogging(val client: Client) {
    companion object {
        fun fromJson(json: JsonObject): PistonLogging {
            return PistonLogging(Client.fromJson(json.getAsJsonObject("client")))
        }
    }

    data class Client(val argument: String, val file: LoggingFile, val type: String) {
        companion object {
            fun fromJson(json: JsonObject): Client {
                return Client(
                    json.get("argument").asString,
                    LoggingFile.fromJson(json.getAsJsonObject("file")),
                    json.get("type").asString
                )
            }
        }
    }

    data class LoggingFile(val id: String, val sha1: String, val size: Long, val url: String) {
        companion object {
            fun fromJson(json: JsonObject): LoggingFile {
                return MCLauncher.GSON.fromJson(json, LoggingFile::class.java)
            }
        }
    }
}