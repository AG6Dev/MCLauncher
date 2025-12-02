package dev.ag6.mclauncher.minecraft.piston

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.minecraft.Type

data class PistonVersionMetadata(
    val arguments: PistonArguments,
    val assetIndex: PistonAssetIndex,
    val assets: String,
    val downloads: PistonDownloads,
    val id: String,
    val javaVersion: PistonJavaVersion,
    val libraries: List<PistonLibrary>,
    val logging: PistonLogging,
    val mainClass: String,
    val complianceLevel: Int,
    val minimumLauncherVersion: Int,
    val releaseTime: String,
    val time: String,
    val type: Type
) {

    //TODO: Parse compliance info and handle correctly
    companion object {
        fun fromJson(jsonObject: JsonObject): PistonVersionMetadata {
            val librariesJsonObject = jsonObject.getAsJsonArray("libraries")
            val arguments = jsonObject.getAsJsonObject("arguments") ?: jsonObject.getAsJsonObject("minecraftArguments")

            return PistonVersionMetadata(
                arguments = MCLauncher.GSON.fromJson(
                    arguments,
                    PistonArguments::class.java
                ),
                assetIndex = PistonAssetIndex.fromJson(jsonObject.getAsJsonObject("assetIndex")),
                assets = jsonObject.get("assets").asString,
                downloads = PistonDownloads.fromJson(jsonObject.getAsJsonObject("downloads")),
                id = jsonObject.get("id").asString,
                javaVersion = PistonJavaVersion.fromJson(jsonObject.getAsJsonObject("javaVersion")),
                libraries = librariesJsonObject.map { PistonLibrary.fromJson(it.asJsonObject) },
                logging = PistonLogging.fromJson(jsonObject.getAsJsonObject("logging")),
                mainClass = jsonObject.get("mainClass").asString,
                minimumLauncherVersion = jsonObject.get("minimumLauncherVersion").asInt,
                releaseTime = jsonObject.get("releaseTime").asString,
                time = jsonObject.get("time").asString,
                type = Type.fromString(jsonObject.get("type").asString),
                complianceLevel = jsonObject.get("complianceLevel").asInt
            )
        }
    }
}