package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher

data class Download(val path: String?, val sha1: String, val size: Long, val url: String) {
    companion object {
        fun fromJson(json: JsonObject): Download {
            return MCLauncher.GSON.fromJson(json, Download::class.java)
        }
    }
}