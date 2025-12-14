package dev.ag6.mclauncher.view.instance

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.instance.InstanceManager
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.util.createSpacer
import dev.ag6.mclauncher.util.getImageOrDefault
import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.components.ConfirmActionWindow
import dev.ag6.mclauncher.view.instance_settings.InstanceSettingsView
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color

class InstanceListItem(private val instance: GameInstance) : HBox() {
    init {
        val buttonContainer = createButtonContainer()

        setOnMouseEntered {
            border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
            buttonContainer.isVisible = true
        }
        setOnMouseExited {
            border = Border.EMPTY
            buttonContainer.isVisible = false
        }

        children += createInfoBox()
        children += createSpacer()
        children += buttonContainer
    }

    private fun createInfoBox(): HBox = HBox(10.0).apply {
        alignment = Pos.CENTER
        children += createIcon()
        children += createInfo()
    }

    private fun createIcon(): ImageView = ImageView(getImageOrDefault(instance.icon, "default_icons/grass.png")).apply {
        fitWidth = 48.0
        fitHeight = 48.0
        isPreserveRatio = true
    }


    private fun createInfo(): VBox = VBox().apply {
        alignment = Pos.CENTER_LEFT
        children += Label(instance.name)
        children += Label(instance.version?.id)
    }

    private fun createButtonContainer(): HBox = HBox(10.0).apply {
        isVisible = false
        alignment = Pos.CENTER
        //TEMP
        children += createButton("Settings") { ContentManager.changeView(InstanceSettingsView(instance)) }
        children += createButton("Run") { InstanceLauncher.launchInstance(instance) }
        children += createButton("Delete") {
            ConfirmActionWindow(
                "Are you sure you want to delete the instance \"${instance.name}\"? \n This action cannot be undone.",
                title = "Confirm Instance Deletion"
            ) {
                InstanceManager.deleteInstance(
                    instance
                )
            }
        }
    }

    private fun createButton(text: String, action: EventHandler<ActionEvent>): Button = Button(text).apply {
        onAction = action
    }
}
