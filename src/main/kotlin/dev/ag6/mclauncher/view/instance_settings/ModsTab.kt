package dev.ag6.mclauncher.view.instance_settings

import dev.ag6.mclauncher.instance.GameInstance
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.VBox

class ModsTab(private val instance: GameInstance) : Tab() {
    init {
        text = "Mods"
        content = createScrollContainer()
    }

    private fun createScrollContainer(): ScrollPane = ScrollPane().apply {
        content = createContainer()
        isFitToWidth = true
        isFitToHeight = true
    }

    private fun createContainer(): VBox = VBox().apply {
        spacing = 10.0

        children += createModLoaderSelection()
    }

    private fun createModLoaderSelection(): ChoiceBox<String> = ChoiceBox<String>().apply {
        text = "Select Mod Loader"
        items.addAll("None", "Fabric")
    }
}