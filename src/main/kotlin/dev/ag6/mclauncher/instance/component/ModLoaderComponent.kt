package dev.ag6.mclauncher.instance.component

import dev.ag6.mclauncher.launch.InstanceLaunchContext

interface ModLoaderComponent : LaunchComponent, ConfigurableComponent {
    fun getMods(): List<Mod>

    override fun addToClasspath(ctx: InstanceLaunchContext, classpath: MutableList<String>) {
//        classpath.addAll(getMods().map { it.path })
    }
}