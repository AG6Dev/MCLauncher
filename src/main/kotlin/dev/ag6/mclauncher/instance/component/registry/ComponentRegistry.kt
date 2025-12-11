package dev.ag6.mclauncher.instance.component.registry

import dev.ag6.mclauncher.instance.component.Component
import dev.ag6.mclauncher.instance.component.JavaComponent
import dev.ag6.mclauncher.instance.component.fabric.FabricLoaderComponent
import dev.ag6.mclauncher.instance.component.minecraft.MinecraftGameComponent

object ComponentRegistry {
    private val components: MutableMap<String, () -> Component> = mutableMapOf()

    init {
        registerComponent("java") { JavaComponent() }
        registerComponent("minecraft") { MinecraftGameComponent() }
        registerComponent("fabric") { FabricLoaderComponent() }
    }

    private fun registerComponent(id: String, factory: () -> Component) {
        components[id] = factory
    }

    fun createComponent(id: String): Component? {
        val factory = components[id] ?: return null
        return factory()
    }
}