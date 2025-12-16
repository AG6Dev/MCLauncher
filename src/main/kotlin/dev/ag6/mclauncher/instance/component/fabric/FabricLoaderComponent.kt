package dev.ag6.mclauncher.instance.component.fabric

import com.google.gson.JsonObject
import dev.ag6.mclauncher.instance.component.MainClassProvider
import dev.ag6.mclauncher.instance.component.Mod
import dev.ag6.mclauncher.instance.component.ModLoaderComponent
import dev.ag6.mclauncher.instance.component.fabric.meta.FabricLauncherMeta
import dev.ag6.mclauncher.launch.InstanceLaunchContext

class FabricLaunchData(
    val fabricLauncherMeta: FabricLauncherMeta
)

class FabricLoaderComponent : ModLoaderComponent, MainClassProvider {
    override fun getMods(): List<Mod> {
        TODO("Not yet implemented")
    }

    override fun getComponentId(): String {
        return "fabric"
    }

    override fun saveConfigData(data: JsonObject) {
        TODO("Not yet implemented")
    }

    override fun loadConfigData(data: JsonObject) {
        TODO("Not yet implemented")
    }

    override fun getMainClass(ctx: InstanceLaunchContext): String {
        TODO("Not yet implemented")
    }

}