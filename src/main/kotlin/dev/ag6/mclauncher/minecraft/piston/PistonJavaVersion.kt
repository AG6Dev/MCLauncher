package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher

data class PistonJavaVersion(val component: String?, val majorVersion: Int) {
    companion object {
        fun fromJson(json: JsonObject): PistonJavaVersion {
            return MCLauncher.GSON.fromJson(json, PistonJavaVersion::class.java)
        }
    }
}