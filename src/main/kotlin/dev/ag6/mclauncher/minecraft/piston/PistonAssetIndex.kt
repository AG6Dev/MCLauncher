package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher

data class PistonAssetIndex(val id: String, val sha1: String, val size: Long, val totalSize: Long, val url: String) {
    companion object {
        fun fromJson(json: JsonObject): PistonAssetIndex {
            return MCLauncher.GSON.fromJson(json, PistonAssetIndex::class.java)
        }
    }
}
