package dev.ag6.mclauncher.view.components

import dev.ag6.mclauncher.util.ProcessStreamReader
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font

class ProcessOutputConsolePane(process: Process) : AnchorPane() {
    private val processStreamReader = ProcessStreamReader(process)
    private val textArea = createTextArea()

    init {
        processStreamReader.lines.addListener(ListChangeListener { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    for (line in change.addedSubList) {
                        appendLine(line)
                    }
                }
            }
        })
        children += textArea
    }

    private fun createTextArea(): TextArea = TextArea().apply {
        isEditable = false
        font = Font.font("Consolas", 14.0)
        isWrapText = true

        setTopAnchor(this, 0.0)
        setBottomAnchor(this, 0.0)
        setLeftAnchor(this, 0.0)
        setRightAnchor(this, 0.0)
    }

    private fun appendLine(text: String) = Platform.runLater {
        textArea.appendText("$text\n")
    }
}