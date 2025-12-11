package dev.ag6.mclauncher.instance.component

interface ModLoaderComponent : LaunchComponent {
    fun getMods(): List<Mod>
}