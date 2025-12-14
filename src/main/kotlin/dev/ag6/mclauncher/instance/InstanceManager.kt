package dev.ag6.mclauncher.instance

import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.util.getDefaultDataLocation
import dev.ag6.mclauncher.util.toPath
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object InstanceManager {
    val INSTANCE_DIRECTORY: Path = getDefaultDataLocation().resolve("instances")
    val instances: ObservableList<GameInstance> = FXCollections.observableArrayList()

    fun loadAllInstances() {
        try {
            if (INSTANCE_DIRECTORY.exists().not()) {
                INSTANCE_DIRECTORY.createDirectories()
                return
            }

            Files.list(INSTANCE_DIRECTORY).forEach {
                if (it.isDirectory()) {
                    val result = GameInstance.fromDirectory(it)
                    if (result.isSuccess()) {
                        instances.add(result.instance)
                    } else {
                        MCLauncher.LOGGER.error(result.error?.exception) { result.error?.message }
                    }
                }
            }
        } catch (e: Exception) {
            MCLauncher.LOGGER.error(e) { "Failed to load instances" }
        }
    }

    fun createInstance(name: String, version: MinecraftVersion): GameInstance {
        val actualName = verifyInstanceName(name)
        val newInstance =
            GameInstance(
                name = actualName,
                version = version,
                directory = INSTANCE_DIRECTORY.resolve(actualName).toString()
            )
        newInstance.createDirectories()
        newInstance.addDefaultComponents()
        newInstance.save()

        instances.add(newInstance)
        return newInstance
    }

    fun updateInstance(old: GameInstance, new: GameInstance) {
        val index = instances.indexOf(old)
        if (index != -1) {
            instances[index] = new
            new.save()
        } else {
            MCLauncher.LOGGER.warn { "Could not update instance ${old.id} as its index is -1" }
        }
    }

    fun saveAllInstances() {
        instances.forEach(GameInstance::save)
    }

    fun deleteInstance(instance: GameInstance) {
        try {
            val instanceDir = instance.directory
            if (Files.exists(instanceDir.toPath())) {
                Files.walk(instanceDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .forEach(Files::delete)
            }
            instances.remove(instance)
        } catch (e: Exception) {
            instance.save()
            MCLauncher.LOGGER.error(e) { "Failed to delete instance ${instance.id}, undoing" }
        }
    }

    private fun verifyInstanceName(name: String): String {
        val actualName = name.trim().ifEmpty { "New Instance" }
        var uniqueName = actualName
        var counter = 1

        while (instances.any { it.name == uniqueName }) {
            uniqueName = "$actualName ($counter)"
            counter++
        }

        return uniqueName
    }
}
