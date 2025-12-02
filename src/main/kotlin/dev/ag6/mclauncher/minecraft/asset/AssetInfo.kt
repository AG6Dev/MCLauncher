package dev.ag6.mclauncher.minecraft.asset

import java.nio.file.Path

data class AssetInfo(val hash: String, val size: Int) {
    fun getAssetPath(): Path {
        val subDir = hash.substring(0, 2)
        return Path.of(subDir, hash)
    }
}