package dev.ag6.mclauncher.view.instance

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.launch.InstanceLauncher
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.layout.*
import javafx.scene.paint.Color

class InstanceGridItem(private val gameInstance: GameInstance) : VBox() {
    init {
        alignment = Pos.CENTER
        prefWidth = 270.0
        prefHeight = 180.0
        padding = Insets(15.0)

        background = Background(BackgroundFill(Color.web("#3C3F41"), CornerRadii(8.0), Insets.EMPTY))
        border =
            Border(BorderStroke(Color.web("#4A4D4F"), BorderStrokeStyle.SOLID, CornerRadii(8.0), BorderWidths.DEFAULT))
        effect = DropShadow(BlurType.GAUSSIAN, Color.color(0.0, 0.0, 0.0, 0.4), 10.0, 0.0, 0.0, 0.0)
        cursor = Cursor.HAND

        onMouseReleased = EventHandler {
            InstanceLauncher.launchInstance(gameInstance)
        }

        children += createNameLabel()
        children += createVersionLabel()
    }

    private fun createNameLabel(): Label = Label(gameInstance.name).apply {
        textFill = Color.WHITE
        isWrapText = true
        alignment = Pos.CENTER
    }

    private fun createVersionLabel(): Label = Label(gameInstance.version?.id).apply {
        textFill = Color.WHITE
    }
}