package dev.ag6.mclauncher.util

import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.InputStream

class WindowCreator {
    var title: String = ""

    var width: Int = -1
    var height: Int = -1
    var minWidth: Int = -1
    var minHeight: Int = -1
    var maxWidth: Int = -1
    var maxHeight: Int = -1

    var resizable: Boolean = true
    var fullscreen: Boolean = false
    var maximized: Boolean = false
    var stageStyle: StageStyle = StageStyle.DECORATED
    var owner: Stage? = null
    var modality: Modality? = null

    var icon: InputStream? = null

    var scene: Scene? = null
    var onCreate: (Stage) -> Unit = {}

    companion object {
        fun create(block: WindowCreator.() -> Unit): Stage {
            val stage = Stage()
            create(stage, block)
            return stage
        }

        fun create(stage: Stage, block: WindowCreator.() -> Unit) {
            val creator = WindowCreator().apply(block)
            stage.title = creator.title
            if (creator.width > 0 && creator.height > 0) {
                stage.width = creator.width.toDouble()
                stage.height = creator.height.toDouble()
            }

            if (creator.minWidth > 0) {
                stage.minWidth = creator.minWidth.toDouble()
            }

            if (creator.minHeight > 0) {
                stage.minHeight = creator.minHeight.toDouble()
            }

            if (creator.maxWidth > 0) {
                stage.maxWidth = creator.maxWidth.toDouble()
            }

            if (creator.maxHeight > 0) {
                stage.maxHeight = creator.maxHeight.toDouble()
            }

            stage.isResizable = creator.resizable
            stage.isFullScreen = creator.fullscreen
            stage.isMaximized = creator.maximized

            stage.initStyle(creator.stageStyle)

            creator.icon?.let {
                stage.icons.add(Image(it))
            }

            creator.scene?.let {
                stage.scene = it
            }

            creator.owner?.let {
                stage.initOwner(it)
            }

            creator.modality?.let {
                stage.initModality(it)
            }

            creator.onCreate(stage)
        }

        fun destroyWindow(stage: Stage) {
            stage.close()
        }
    }
}