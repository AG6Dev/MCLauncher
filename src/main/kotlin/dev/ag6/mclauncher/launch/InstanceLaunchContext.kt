package dev.ag6.mclauncher.launch

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.minecraft.asset.AssetIndex
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import dev.ag6.mclauncher.task.TaskExecutor
import java.nio.file.Path

class InstanceLaunchContext(val instance: GameInstance, val version: MinecraftVersion = instance.version!!) {
    val taskExecutor = TaskExecutor()
    lateinit var pistonMeta: PistonVersionMetadata
    lateinit var assetIndex: AssetIndex
    lateinit var libraries: List<Path>
    lateinit var clientJar: Path
}