package dev.ag6.mclauncher.view.components

import dev.ag6.mclauncher.util.WindowCreator
import dev.ag6.mclauncher.view.ContentManager
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage

class ConfirmActionWindow(
    private val message: String,
    title: String = "Confirm Action",
    private val onConfirm: () -> Unit
) {
    private val stage: Stage = WindowCreator.create {
        this.title = title
        modality = Modality.WINDOW_MODAL
        owner = ContentManager.stage
        resizable = false
        scene = Scene(createContainer())
    }

    init {
        stage.show()
    }

    private fun createContainer(): HBox = HBox().apply {
        padding = Insets(20.0)
        children += Label(message)
        children += createButtonContainer()
    }

    private fun createButtonContainer(): HBox = HBox(10.0).apply {
        padding = Insets(10.0)
        alignment = Pos.BOTTOM_CENTER
        children += createButton("Confirm") {
            onConfirm()
            WindowCreator.destroyWindow(stage)
        }
        children += createButton("Cancel") { WindowCreator.destroyWindow(stage) }
    }

    private fun createButton(text: String, func: EventHandler<ActionEvent>): Button = Button(text).apply {
        onAction = func
    }
}