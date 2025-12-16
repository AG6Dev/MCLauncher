package dev.ag6.mclauncher.instance.component.minecraft

import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.instance.component.ConfigurableComponent
import dev.ag6.mclauncher.instance.component.LaunchComponent
import dev.ag6.mclauncher.instance.component.MainClassProvider
import dev.ag6.mclauncher.instance.settings.NumberSetting
import dev.ag6.mclauncher.instance.settings.Setting
import dev.ag6.mclauncher.instance.settings.SettingCategory
import dev.ag6.mclauncher.launch.InstanceLaunchContext
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.launch.tasks.*
import dev.ag6.mclauncher.minecraft.piston.PistonLibrary
import dev.ag6.mclauncher.task.CompositeTask
import java.nio.file.Path

class MinecraftGameComponent : LaunchComponent, ConfigurableComponent, MainClassProvider {
    private val customWidth = NumberSetting(
        "Custom Window Width",
        "The custom width of the game window. Set to 0 to use default.",
        defaultValue = 0,
        category = SettingCategory.OTHER
    )

    private val customHeight = NumberSetting(
        "Custom Window Height",
        "The custom height of the game window. Set to 0 to use default.",
        defaultValue = 0,
        category = SettingCategory.OTHER
    )

    override fun getComponentId(): String {
        return "minecraft"
    }

    @Suppress("unchecked_cast")
    override suspend fun prepareLaunch(ctx: InstanceLaunchContext) {
        val executor = ctx.taskExecutor

        ctx.pistonMeta = executor.submit(FetchVersionMetadataTask(ctx.version)).await()

        val meta = ctx.pistonMeta
        ctx.libraries =
            executor.submit(CompositeTask("Download Libraries", true, getLibrariesForSystem(meta.libraries)))
                .await() as List<Path>
        ctx.clientJar = executor.submit(FetchMinecraftJarTask(meta)).await()
        ctx.assetIndex = executor.submit(FetchAssetIndexTask(meta)).await()

        val assetTasks = ctx.assetIndex.assets.values.map(::DownloadAssetTask)
        executor.submit(CompositeTask("Download Game Assets", true, assetTasks)).await()
    }

    override fun addGameArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {
        fun putArgument(key: String, value: String) {
            args.add("--$key")
            args.add(value)
        }

        /* gameArgumentMap["auth_player_name"] = "Player"
           gameArgumentMap["auth_uuid"] = ""
           gameArgumentMap["auth_access_token"] = ""
           gameArgumentMap["auth_xuid"] = ""

           gameArgumentMap["quickPlayPath"] = ""
           gameArgumentMap["quickPlaySingleplayer"] = ""
           gameArgumentMap["quickPlayMultiplayer"] = ""
           gameArgumentMap["quickPlayRealms"] = ""
                */

        putArgument("version", ctx.version.id)
        putArgument("accessToken", "\"\"")

        putArgument("versionType", ctx.pistonMeta.type.name.lowercase())
        putArgument("gameDir", ctx.instance.getMinecraftDirectory().toString())
        putArgument("assetsDir", InstanceLauncher.ASSETS_LOCATION.toString())
        putArgument("assetIndex", ctx.assetIndex.id)

        customWidth.takeIf { it.value.toInt() > 0 }?.let { putArgument("width", customWidth.toString()) }
        customHeight.takeIf { it.value.toInt() > 0 }?.let { putArgument("height", customHeight.toString()) }
    }

    override fun addJvmArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {
        val nativesDir = ctx.instance.getMinecraftDirectory().resolve("natives")

        args.addAll(
            listOf(
                "-Djava.library.path\u003d${nativesDir}",
                "-Djna.tmpdir\u003d${nativesDir}",
                "-Dorg.lwjgl.system.SharedLibraryExtractPath\u003d${nativesDir}",
                "-Dio.netty.native.workdir\u003d${nativesDir}",
                "-Dminecraft.launcher.brand\u003d${MCLauncher.BRAND}",
                "-Dminecraft.launcher.version\u003d${MCLauncher.VERSION}"
            )
        )
    }

    override fun addToClasspath(ctx: InstanceLaunchContext, classpath: MutableList<String>) {
        ctx.libraries.map { it.toString() }.forEach(classpath::add)
        classpath.add(ctx.clientJar.toString())
    }

    override fun getMainClass(ctx: InstanceLaunchContext): String {
        return ctx.pistonMeta.mainClass
    }

    override fun saveConfigData(data: JsonObject) {
        data.addProperty("customWidth", customWidth.value)
        data.addProperty("customHeight", customHeight.value)
    }

    override fun loadConfigData(data: JsonObject) {
        customWidth.value = data.getAsJsonPrimitive("customWidth")?.asInt ?: 0
        customHeight.value = data.getAsJsonPrimitive("customHeight")?.asInt ?: 0
    }

    override fun getSettings(): List<Setting<*>> = listOf(
        customWidth,
        customHeight
    )

    private fun getLibrariesForSystem(allLibs: List<PistonLibrary>): List<FetchLibraryTask> {
        return allLibs.filter { it.isAllowedForSystem() }.map(::FetchLibraryTask)
    }
}