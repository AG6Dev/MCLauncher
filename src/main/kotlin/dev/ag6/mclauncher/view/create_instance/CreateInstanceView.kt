package dev.ag6.mclauncher.view.create_instance

import dev.ag6.mclauncher.instance.InstanceManager
import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.View
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region

class CreateInstanceView : View {
    private val container: BorderPane = BorderPane()

    override fun build(): Region {
        val backButton = Button().apply {
            text = "Cancel"
            prefWidth = 100.0
            onAction = EventHandler { ContentManager.goBack() }
        }

        val nameField = TextField().apply {
            promptText = "Instance Name"
            prefWidth = 200.0
        }

        val versionSelector = VersionSelectorList()

        val createButton: Button = Button().apply {
            text = "Create"
            prefWidth = 100.0
            onAction = EventHandler {
                InstanceManager.createInstance(nameField.text, versionSelector.getSelectedVersion())
                ContentManager.goBack()
            }
        }

        val buttonContainer = HBox().apply {
            spacing = 10.0
            alignment = Pos.CENTER
        }
        buttonContainer.children.addAll(backButton, createButton)

        container.center = HBox().apply {
            spacing = 10.0
            alignment = Pos.CENTER
            children.addAll(nameField, versionSelector)
        }
        container.bottom = buttonContainer

        return container
    }
}