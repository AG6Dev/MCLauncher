package dev.ag6.mclauncher.instance.component.fabric.meta

data class FabricLauncherMeta(
    val version: Int,
    val minJavaVersion: Int,
    val libraries: FabricLibraries,
    val mainClass: FabricMainClass
)

data class FabricLibraries(val client: List<FabricLibrary> = emptyList(), val server: List<FabricLibrary> = emptyList())

data class FabricLibrary(
    val name: String,
    val url: String,
    val md5: String,
    val sha1: String,
    val sha256: String,
    val sha512: String,
    val size: Long
)

data class FabricMainClass(val client: String, val server: String)
