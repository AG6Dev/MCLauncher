package dev.ag6.mclauncher.launch

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.minecraft.asset.AssetIndex
import dev.ag6.mclauncher.minecraft.piston.PistonVersionMetadata
import dev.ag6.mclauncher.task.TaskExecutor
import java.nio.file.Path

@Suppress("UNCHECKED_CAST")
class InstanceLaunchContext(val instance: GameInstance, val version: MinecraftVersion = instance.version!!) {
    val taskExecutor = TaskExecutor()
    lateinit var pistonMeta: PistonVersionMetadata
    lateinit var assetIndex: AssetIndex
    lateinit var libraries: List<Path>
    lateinit var clientJar: Path

    private val data: MutableMap<Any, Any> = mutableMapOf()

    fun <T : Any> put(key: Class<T>, value: T) {
        data[key] = value
    }

    fun <T : Any> get(key: Class<T>): T {
        return data[key] as T
    }

    fun <T : Any> getOrNull(key: Class<T>): T? {
        return data[key] as T?
    }
}