package dev.ag6.mclauncher.instance.component.fabric

import dev.ag6.mclauncher.instance.component.MainClassProvider
import dev.ag6.mclauncher.instance.component.Mod
import dev.ag6.mclauncher.instance.component.ModLoaderComponent
import dev.ag6.mclauncher.launch.InstanceLaunchContext

class FabricLaunchData(
    val mainClass: String
)

class FabricLoaderComponent : ModLoaderComponent, MainClassProvider {
    private val mods: MutableList<Mod> = mutableListOf()

    override fun getComponentId(): String {
        return "fabric"
    }

    override fun getMods(): List<Mod> {
        TODO("Not yet implemented")
    }

    override suspend fun prepareLaunch(ctx: InstanceLaunchContext) {
        ctx.taskExecutor.submit(FetchFabricMetadataTask(ctx.version, "0.18.1")).await().let {
            val launchData = FabricLaunchData(it.launcherMeta.mainClass.client)
            ctx.put(FabricLaunchData::class.java, launchData)
        }
    }

    override fun addToClasspath(ctx: InstanceLaunchContext, classpath: MutableList<String>) {
        val fabricData = ctx.get(FabricLaunchData::class.java)
    }

    override fun getMainClass(ctx: InstanceLaunchContext): String {
        return ctx.get(FabricLaunchData::class.java).mainClass
    }
}