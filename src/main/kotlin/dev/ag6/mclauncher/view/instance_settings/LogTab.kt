package dev.ag6.mclauncher.view.instance_settings

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.launch.InstanceLauncher
import dev.ag6.mclauncher.view.components.ProcessOutputConsolePane
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane

class LogTab(private val instance: GameInstance) : Tab() {

    init {
        text = "Logs"
        isClosable = false

        setOnSelectionChanged {
            if (isSelected) {
                buildContent()
            }
        }
    }

    private fun buildContent() {
        val process = InstanceLauncher.launchProcesses[instance]

        if (process != null && process.isAlive) {
            content = ProcessOutputConsolePane(process)
        } else {
            content = BorderPane().apply {
                center = Label("Instance is not running.").apply {
                    alignment = Pos.CENTER
                }
            }
        }
    }
}
