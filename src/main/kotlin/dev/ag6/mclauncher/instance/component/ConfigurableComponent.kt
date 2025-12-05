package dev.ag6.mclauncher.instance.component

import com.google.gson.JsonObject
import dev.ag6.mclauncher.instance.component.settings.Setting

interface ConfigurableComponent : Component {
    fun getSettings(): List<Setting<*>> = emptyList()

    fun saveConfigData(data: JsonObject)

    fun loadConfigData(data: JsonObject)
}