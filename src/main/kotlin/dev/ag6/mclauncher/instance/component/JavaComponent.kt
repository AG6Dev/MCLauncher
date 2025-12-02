package dev.ag6.mclauncher.instance.component

import com.google.gson.JsonObject
import dev.ag6.mclauncher.launch.InstanceLaunchContext

class JavaComponent : LaunchComponent, ConfigurableComponent {
    var minAlloc: Int = 2048
    var maxAlloc: Int = 4096
    var javaPath: String = "java"

    override fun getComponentId(): String {
        return "java"
    }

    override fun addJvmArgs(launchContext: InstanceLaunchContext, args: MutableList<String>) {
        args.add("-Xms${minAlloc}M")
        args.add("-Xmx${maxAlloc}M")
    }

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