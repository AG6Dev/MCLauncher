package dev.ag6.mclauncher.util

import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import java.awt.GraphicsEnvironment

infix fun <T : Node> T.styleAs(style: String): T = apply { styleClass += style }

infix fun <T : Node> T.addStyleSheet(styleSheet: String): T = apply {
    scene.stylesheets.add(styleSheet)
}

fun createSpacer(): Region = Region().apply {
    HBox.setHgrow(this, Priority.ALWAYS)
    VBox.setVgrow(this, Priority.ALWAYS)
}

fun getRefreshRate(): Int {
    try {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val rate = gd.displayMode.refreshRate
        return if (rate > 0) rate else 60
    } catch (e: Exception) {
        return 60
    }
}