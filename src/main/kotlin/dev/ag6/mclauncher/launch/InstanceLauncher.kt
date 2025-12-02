package dev.ag6.mclauncher.launch

import dev.ag6.mclauncher.instance.GameInstance
import dev.ag6.mclauncher.util.WindowCreator
import dev.ag6.mclauncher.util.getDefaultDataLocation
import dev.ag6.mclauncher.view.components.ProcessOutputConsolePane
import javafx.application.Platform
import javafx.scene.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.time.Instant

object InstanceLauncher {
    val ASSETS_LOCATION: Path = getDefaultDataLocation().resolve("assets")
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val launchProcesses: MutableMap<GameInstance, Process> = mutableMapOf()

    //TODO: Consider creating a LaunchTask class which takes in the launch context
    fun launchInstance(gameInstance: GameInstance) = scope.launch {
        if (gameInstance.version == null) return@launch // TODO: Show error to user via UI popup

        val launchContext = InstanceLaunchContext(gameInstance)

        gameInstance.launchComponents.forEach { it.prepareLaunch(launchContext) }

        val commandBuilder = LaunchProcessBuilder(launchContext)
        val process = commandBuilder.build()

        Platform.runLater {
            val window = WindowCreator.create {
                scene = Scene(ProcessOutputConsolePane(process).apply {
                    prefWidth = 800.0
                    prefHeight = 600.0
                })
            }
            window.show()
        }

        launchProcesses[gameInstance] = process

        gameInstance.lastPlayed = Instant.now().toString()
        gameInstance.save()
    }
}