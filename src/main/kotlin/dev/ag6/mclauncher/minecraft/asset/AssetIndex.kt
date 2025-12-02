package dev.ag6.mclauncher.minecraft.asset

import com.google.gson.JsonObject

data class AssetIndex(val id: String, val assets: Map<String, AssetInfo>) {
    companion object {
        fun fromJson(id: String, json: JsonObject): AssetIndex {
            val assetsJson = json.getAsJsonObject("objects")
            val assets = assetsJson.entrySet().associate { (name, assetJsonElement) ->
                val assetJson = assetJsonElement.asJsonObject
                val hash = assetJson.get("hash").asString
                val size = assetJson.get("size").asInt
                name to AssetInfo(hash, size)
            }
            return AssetIndex(id, assets)
        }
    }
}