package dev.ag6.mclauncher.instance.component

import com.google.gson.JsonObject

interface ConfigurableComponent : Component {
    fun saveConfigData(data: JsonObject)

    fun loadConfigData(data: JsonObject)
}