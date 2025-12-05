package dev.ag6.mclauncher.instance.component

import com.google.gson.JsonObject
import dev.ag6.mclauncher.instance.component.settings.NumberSetting
import dev.ag6.mclauncher.instance.component.settings.Setting
import dev.ag6.mclauncher.instance.component.settings.SettingCategory
import dev.ag6.mclauncher.instance.component.settings.StringSetting
import dev.ag6.mclauncher.launch.InstanceLaunchContext
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class JavaComponent : ConfigurableComponent, LaunchComponent {
    private val _minAlloc = SimpleIntegerProperty(2048)
    private var minAlloc: Int
        get() = _minAlloc.get()
        set(value) = _minAlloc.set(value)

    private val _maxAlloc = SimpleIntegerProperty(4096)
    private var maxAlloc: Int
        get() = _maxAlloc.get()
        set(value) = _maxAlloc.set(value)

    private val _javaPath = SimpleStringProperty("java")
    var javaPath: String
        get() = _javaPath.get()
        set(value) = _javaPath.set(value)


    override fun getComponentId(): String {
        return "java"
    }

    override fun addJvmArgs(ctx: InstanceLaunchContext, args: MutableList<String>) {
        args.add("-Xms${minAlloc}M")
        args.add("-Xmx${maxAlloc}M")
    }

    override fun getSettings(): List<Setting<*>> = listOf(
        NumberSetting(
            "Minimum Memory Allocation",
            "The minimum amount of memory to allocate to the game.",
            _minAlloc,
            SettingCategory.JAVA
        ),
        NumberSetting(
            "Maximum Memory Allocation",
            "The maximum amount of memory to allocate to the game.",
            _maxAlloc,
            SettingCategory.JAVA
        ),
        StringSetting(
            "Java Path", "The path to the java executable.", _javaPath, SettingCategory.JAVA
        ),
    )

    override fun saveConfigData(data: JsonObject) {
        data.addProperty("minAlloc", minAlloc)
        data.addProperty("maxAlloc", maxAlloc)
        data.addProperty("javaPath", javaPath)
    }

    override fun loadConfigData(data: JsonObject) {
        this.minAlloc = data.get("minAlloc").asInt
        this.maxAlloc = data.get("maxAlloc").asInt
        this.javaPath = data.get("javaPath").asString
    }
}