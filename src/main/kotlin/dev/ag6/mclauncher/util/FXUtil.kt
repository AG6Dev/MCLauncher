package dev.ag6.mclauncher.util

import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import java.awt.GraphicsEnvironment

fun getImageOrDefault(url: String?, default: String): Image = try {
    Image(url, true)
} catch (e: Exception) {
    Image(default, true)
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