package dev.ag6.mclauncher.view

import dev.ag6.mclauncher.MCLauncher.Companion.VERSION
import dev.ag6.mclauncher.util.WindowCreator
import dev.ag6.mclauncher.util.getResourceStream
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

object ContentManager {
    private val windowPane: BorderPane = BorderPane()
    private val navigationStack: ArrayDeque<View> = ArrayDeque()
    lateinit var stage: Stage
        private set

    fun init(stage: Stage) {
        this.stage = stage

        windowPane.apply {
            padding = Insets(10.0, 10.0, 10.0, 10.0)
        }

        WindowCreator.create(stage) {
            title = "MCLauncher $VERSION"
            icon = getResourceStream("icon.png")

            minWidth = 800
            minHeight = 600

            width = 1280
            height = 720

            scene = Scene(windowPane)
        }
    }

    fun changeView(newView: View) = Platform.runLater {
        navigationStack.addLast(newView)
        windowPane.center = newView.build()
    }

    fun goBack() = Platform.runLater {
        if (navigationStack.size <= 1) return@runLater

        navigationStack.removeLast()
        val previousView = navigationStack.last()
        windowPane.center = previousView.build()
    }

    fun show() = Platform.runLater {
        stage.show()
    }
}