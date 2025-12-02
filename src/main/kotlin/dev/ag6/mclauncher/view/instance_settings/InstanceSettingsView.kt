package dev.ag6.mclauncher.view.instance_settings

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.instance.InstanceManager
import dev.ag6.mclauncher.instance.component.JavaComponent
import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.View
import dev.ag6.mclauncher.view.components.ContentBackButton
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.text.Font

class InstanceSettingsView(private val original: GameInstance) : View {
    private val editedInstance: GameInstance = original.clone()

    override fun build(): Region {
        return createContainer()
    }

    private fun createContainer(): BorderPane = BorderPane().apply {
        top = createHeaderBox()
        center = createOptions()
        bottom = createNavigationButtons()
    }

    private fun createOptions(): TabPane = TabPane().apply {
//        tabs += createConsoleTab()
        tabs += createGeneralTab()
    }

    private fun createOptionField(text: String, onType: (field: TextField, currentText: String) -> Unit): TextField =
        TextField().apply {
            onKeyTyped = EventHandler { onType(this, this.text) }
            promptText = text
            setText(editedInstance.getComponent<JavaComponent>()?.javaPath)
        }

    private fun createNavigationButtons(): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER
        children += ContentBackButton("Cancel")
        children += createApplyButton()
    }

    private fun createApplyButton(): Button = Button("Apply").apply {
        onAction = EventHandler {
            InstanceManager.updateInstance(original, editedInstance)
            ContentManager.goBack()
        }
    }

    private fun createHeaderBox(): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER_LEFT
        children += ImageView(original.icon ?: "default_icons/grass.png").apply {
            fitWidth = 64.0
            fitHeight = 64.0
        }
        children += Label("Settings for ${original.name}").apply {
            font = Font.font(32.0)
        }
    }

    //TODO: This is temporary until I create the instance component system
    private fun createGeneralTab(): Tab = Tab("General").apply {
        isClosable = false
        content = ScrollPane().apply {
            content = VBox(10.0).apply {
                padding = Insets(10.0, 0.0, 0.0, 0.0)
                children += createOptionField("Java Path (java.exe/java)") { field, text ->
                    editedInstance.getComponent<JavaComponent>()?.javaPath = text
                }
            }
        }
    }
}