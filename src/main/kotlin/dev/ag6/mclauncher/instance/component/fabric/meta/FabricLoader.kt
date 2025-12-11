package dev.ag6.mclauncher.instance.component.fabric.meta

data class FabricLoader(
    val separator: String,
    val build: Int,
    val maven: String,
    val version: String,
    val stable: Boolean
)
