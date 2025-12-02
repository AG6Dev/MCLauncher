package dev.ag6.mclauncher.instance.component

import dev.ag6.mclauncher.launch.InstanceLaunchContext
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.launch.tasks.*
import dev.ag6.mclauncher.minecraft.piston.PistonLibrary
import dev.ag6.mclauncher.task.CompositeTask
import dev.ag6.mclauncher.util.toPath
import java.nio.file.Path

class MinecraftGameComponent : LaunchComponent, MainClassProvider {
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
        /* gameArgumentMap["auth_player_name"] = "Player"
           gameArgumentMap["auth_uuid"] = ""
           gameArgumentMap["auth_access_token"] = ""
           gameArgumentMap["auth_xuid"] = ""

           gameArgumentMap["version_name"] = launchContext.version.id
           gameArgumentMap["gameDir"] = launchContext.instance.getMinecraftDirectory().toString()
           gameArgumentMap["asset_root"] = launchContext.toString()
           gameArgumentMap["assets_index_name"] = launchContext.assetIndex.id
           gameArgumentMap["clientid"] = launchContext.version.id

           gameArgumentMap["resolution_width"] = "1280"
           gameArgumentMap["resolution_height"] = "720"

           gameArgumentMap["quickPlayPath"] = ""
           gameArgumentMap["quickPlaySingleplayer"] = ""
           gameArgumentMap["quickPlayMultiplayer"] = ""
           gameArgumentMap["quickPlayRealms"] = ""
                */

        args.add("--version")
        args.add(ctx.version.id)
        args.add("--accessToken")
        args.add("\"\"")
        args.add("--assetsDir")
        args.add(InstanceLauncher.ASSETS_LOCATION.toString())
        args.add("--assetIndex")
        args.add(ctx.assetIndex.id)
    }

    override fun addJvmArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {
        args.addAll(
            listOf(
                "-Djava.library.path\u003d${ctx.instance.directory.toPath().resolve("natives")}",
                "-Djna.tmpdir\u003d${ctx.instance.directory.toPath().resolve("natives")}",
                "-Dorg.lwjgl.system.SharedLibraryExtractPath\u003d${
                    ctx.instance.directory.toPath().resolve("natives")
                }",
                "-Dio.netty.native.workdir\u003d${ctx.instance.directory.toPath().resolve("natives")}",
                "-Dminecraft.launcher.brand\u003dMCLauncher",
                "-Dminecraft.launcher.version\u003d1.0.0"
            )
        )
    }

    override fun addToClasspath(ctx: InstanceLaunchContext, classpath: MutableList<String>) {

    }

    override fun getMainClass(ctx: InstanceLaunchContext): String {
        return ctx.pistonMeta.mainClass
    }

    private fun getLibrariesForSystem(allLibs: List<PistonLibrary>): List<FetchLibraryTask> {
        return allLibs.filter { it.isAllowedForSystem() }.map(::FetchLibraryTask)
    }
}