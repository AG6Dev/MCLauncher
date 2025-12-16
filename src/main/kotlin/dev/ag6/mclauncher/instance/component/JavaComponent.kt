package dev.ag6.mclauncher.instance.component

import com.google.gson.JsonObject
import dev.ag6.mclauncher.instance.settings.NumberSetting
import dev.ag6.mclauncher.instance.settings.Setting
import dev.ag6.mclauncher.instance.settings.SettingCategory
import dev.ag6.mclauncher.instance.settings.StringSetting
import dev.ag6.mclauncher.launch.InstanceLaunchContext
import javafx.beans.property.SimpleStringProperty

class JavaComponent : ConfigurableComponent, LaunchComponent {
    private val minAlloc = NumberSetting(
        "Minimum Memory Allocation",
        "The minimum amount of memory to allocate to the game",
        defaultValue = 2048,
        category = SettingCategory.JAVA
    )

    private val maxAlloc = NumberSetting(
        "Maximum Memory Allocation",
        "The maximum amount of memory to allocate to the game.",
        defaultValue = 4096,
        category = SettingCategory.JAVA
    )

    val javaPath = StringSetting(
        "Java Path", "The path to the java executable.", SimpleStringProperty("java"), SettingCategory.JAVA
    )

    override fun getComponentId(): String {
        return "java"
    }

    override fun addJvmArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {
        args.add("-Xms${minAlloc.value}M")
        args.add("-Xmx${maxAlloc.value}M")
    }

    override fun getSettings(): List<Setting<*>> = listOf(
        minAlloc, maxAlloc, javaPath
    )

    override fun saveConfigData(data: JsonObject) {
        data.addProperty("minAlloc", minAlloc.value)
        data.addProperty("maxAlloc", maxAlloc.value)
        data.addProperty("javaPath", javaPath.value)
    }

    override fun loadConfigData(data: JsonObject) {
        this.minAlloc.value = data.get("minAlloc").asInt
        this.maxAlloc.value = data.get("maxAlloc").asInt
        this.javaPath.value = data.get("javaPath").asString
    }
}