package dev.ag6.mclauncher.instance

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.ag6.mclauncher.MCLauncher
import dev.ag6.mclauncher.instance.component.Component
import dev.ag6.mclauncher.instance.component.ConfigurableComponent
import dev.ag6.mclauncher.instance.component.LaunchComponent
import dev.ag6.mclauncher.instance.component.MainClassProvider
import dev.ag6.mclauncher.instance.component.registry.ComponentRegistry
import dev.ag6.mclauncher.instance.component.settings.ChoiceSetting
import dev.ag6.mclauncher.instance.component.settings.Setting
import dev.ag6.mclauncher.instance.component.settings.SettingCategory
import dev.ag6.mclauncher.instance.component.settings.StringSetting
import dev.ag6.mclauncher.minecraft.MinecraftVersion
import dev.ag6.mclauncher.minecraft.MinecraftVersionHandler
import dev.ag6.mclauncher.util.toPath
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories

//TODO: Cache icon images
class GameInstance(
    val id: UUID = UUID.randomUUID(),
    name: String,
    icon: String? = "default_icons/grass.png",
    version: MinecraftVersion?,
    directory: String,
    var lastPlayed: String? = "",
) : Cloneable {
    var directory = directory
        private set

    private val nameProperty = SimpleStringProperty(name)
    var name: String
        get() = nameProperty.get()
        set(value) = nameProperty.set(value)

    private val iconProperty = SimpleStringProperty(icon)
    var icon: String?
        get() = iconProperty.get()
        set(value) = iconProperty.set(value)

    private val versionProperty = SimpleObjectProperty<MinecraftVersion?>(version)
    var version: MinecraftVersion?
        get() = versionProperty.get()
        set(value) = versionProperty.set(value)

    val components: MutableList<Component> = mutableListOf()
    val mainClassProvider: MainClassProvider
        get() = components.filterIsInstance<MainClassProvider>().last()
    val launchComponents: List<LaunchComponent>
        get() = components.filterIsInstance<LaunchComponent>()
    val configurableComponents: List<ConfigurableComponent>
        get() = components.filterIsInstance<ConfigurableComponent>()

    fun addDefaultComponents() {
        components.add(ComponentRegistry.createComponent("java")!!)
        components.add(ComponentRegistry.createComponent("minecraft")!!)
    }

    fun createDirectories() {
        val dir = directory.toPath()
        Files.createDirectories(dir.resolve(".minecraft"))
    }

    fun save() {
        try {
            createDirectories()
            val json = this.toJson()

            val instanceConfigFile = directory.toPath().resolve("instance.json")
            instanceConfigFile.bufferedWriter().use { writer ->
                MCLauncher.GSON.toJson(json, writer)
            }
        } catch (e: Exception) {
            MCLauncher.LOGGER.error(e) { "Error while saving instance $name, $id" }
        }
    }

    private fun renameInstance(newName: String) {
        val newDir = InstanceManager.INSTANCE_DIRECTORY.resolve(newName)
        name = newName
        Files.move(directory.toPath(), newDir)
        directory = newDir.toString()
        save()
    }

    inline fun <reified T : Component> getComponentById(componentId: String): T? {
        return components.filterIsInstance<T>().firstOrNull { it.getComponentId() == componentId }
    }

    private fun toJson(): JsonObject {
        val json = JsonObject()
        //Add all base properties
        json.addProperty("id", id.toString())
        json.addProperty("name", name)
        json.addProperty("icon", icon)
        json.addProperty("version", version?.id)
        json.addProperty("directory", directory)
        json.addProperty("lastPlayed", lastPlayed)

        val components = JsonArray()
        for (component in this.components) {
            val componentObject = JsonObject()
            componentObject.addProperty("type", component.getComponentId())

            if (component is ConfigurableComponent) {
                val configObject = JsonObject()
                component.saveConfigData(configObject)
                componentObject.add("config", configObject)
            }
            components.add(componentObject)
        }

        json.add("components", components)

        return json
    }

    fun getMinecraftDirectory(): Path {
        return directory.toPath().resolve(".minecraft").createDirectories()
    }

    fun getInstanceCoreSettings(): List<Setting<*>> {
        return listOf(
            StringSetting(
                name = "Instance Name",
                description = "The name of this instance.",
                property = nameProperty,
                category = SettingCategory.GENERAL,
                onChange = { _, newValue -> renameInstance(newValue) }
            ),
            StringSetting(
                name = "Icon Path",
                description = "The icon for this instance.",
                property = iconProperty,
                category = SettingCategory.GENERAL
            ),
            //TODO: Make this a proper version selector
            ChoiceSetting(
                name = "Minecraft Version",
                description = "Minecraft Game Version",
                property = versionProperty,
                choices = MinecraftVersionHandler.minecraftVersions,
                category = SettingCategory.GENERAL
            )
        )
    }

    override fun toString(): String {
        return "GameInstance(id=$id, name='$name', version=${version?.id}, directory=$directory, lastPlayed=$lastPlayed)"
    }

    inline fun <reified T : Component> getComponent(): T? {
        return components.filterIsInstance<T>().firstOrNull()
    }

    public override fun clone(): GameInstance {
        return super.clone() as GameInstance
    }

    companion object {
        fun fromJson(json: JsonObject): GameInstance {
            val id = UUID.fromString(json.get("id").asString)
            val name = json.get("name").asString
            val version = json.get("version").asString
            val icon = if (json.has("icon")) json.get("icon").asString else null
            val directory = json.get("directory").asString
            val lastPlayed = if (json.has("lastPlayed")) json.get("lastPlayed").asString else null

            val instance = GameInstance(
                id = id,
                name = name,
                version = MinecraftVersionHandler.getVersion(version),
                icon = icon,
                directory = directory,
                lastPlayed = lastPlayed
            )

            val componentsArray = json.getAsJsonArray("components")
            for (componentObject in componentsArray) {
                val compObj = componentObject.asJsonObject
                val type = compObj.get("type").asString
                val config = compObj.getAsJsonObject("config")

                val component = ComponentRegistry.createComponent(type)
                if (component is ConfigurableComponent) {
                    val configData = MCLauncher.GSON.fromJson(config, JsonObject::class.java)
                    component.loadConfigData(configData)
                }

                if (component != null) {
                    instance.components.add(component)
                }
            }

            return instance
        }

        fun fromDirectory(path: Path): LoadInstanceResult {
            try {
                val configFile = path.resolve("instance.json")
                if (!Files.exists(configFile)) {
                    return LoadInstanceResult.failure(Error("Instance config file not found in directory $path"))
                }

                val instanceJson = Files.newBufferedReader(configFile).use { reader ->
                    MCLauncher.GSON.fromJson(reader, JsonObject::class.java)
                }
                val instance = fromJson(instanceJson)
                return LoadInstanceResult.success(instance)
            } catch (e: Exception) {
                return LoadInstanceResult.failure(Error("Error loading instance from directory $path", e))
            }
        }
    }
}