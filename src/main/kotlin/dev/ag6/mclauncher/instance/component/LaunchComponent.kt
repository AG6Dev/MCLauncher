package dev.ag6.mclauncher.instance.component

import dev.ag6.mclauncher.launch.InstanceLaunchContext

interface LaunchComponent : Component {
    suspend fun prepareLaunch(ctx: InstanceLaunchContext) {}

    fun addGameArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {}

    fun addJvmArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {}

    fun addToClasspath(ctx: InstanceLaunchContext, classpath: MutableList<String>) {}
}