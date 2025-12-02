package dev.ag6.mclauncher.instance.component

import dev.ag6.mclauncher.launch.InstanceLaunchContext

interface MainClassProvider {
    fun getMainClass(ctx: InstanceLaunchContext): String
}