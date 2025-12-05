package dev.ag6.mclauncher

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.ag6.mclauncher.instance.InstanceManager
import dev.ag6.mclauncher.minecraft.MinecraftVersionHandler
import dev.ag6.mclauncher.minecraft.piston.Argument
import dev.ag6.mclauncher.minecraft.piston.ArgumentDeserializer
import dev.ag6.mclauncher.minecraft.piston.ArgumentValue
import dev.ag6.mclauncher.minecraft.piston.ArgumentValueDeserializer
import dev.ag6.mclauncher.util.getDefaultDataLocation
import dev.ag6.mclauncher.util.getRefreshRate
import dev.ag6.mclauncher.view.ContentManager
import dev.ag6.mclauncher.view.instance.InstancesView
import fr.brouillard.oss.cssfx.CSSFX
import io.github.oshai.kotlinlogging.KotlinLogging
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.awt.Desktop
import java.nio.file.Files

class MCLauncher : Application() {
    //TODO: Actually do something with this
    private var firstStart: Boolean = false

    override fun init() {
        if (Files.notExists(getDefaultDataLocation())) {
            Files.createDirectories(getDefaultDataLocation())
            this.firstStart = true
        }
        MinecraftVersionHandler.fetchGameVersions()
        InstanceManager.loadAllInstances()
    }

    override fun start(primaryStage: Stage) {
        CSSFX.start()

        ContentManager.init(primaryStage)
        ContentManager.changeView(InstancesView())
        ContentManager.show()

        //temporary
        primaryStage.scene.setOnKeyPressed { event ->
            if (event.code == KeyCode.F1) {
                Desktop.getDesktop().open(getDefaultDataLocation().toFile())
            }
        }
    }

    override fun stop() = runBlocking {
        InstanceManager.saveAllInstances()

        HTTP_CLIENT.dispatcher.executorService.shutdown()
    }

    companion object {
        const val VERSION: String = "1.0.0"

        val LOGGER = KotlinLogging.logger("MCLauncher")
        val HTTP_CLIENT = OkHttpClient()
        val GSON: Gson = GsonBuilder()
            .registerTypeAdapter(Argument::class.java, ArgumentDeserializer())
            .registerTypeAdapter(ArgumentValue::class.java, ArgumentValueDeserializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create()
    }
}

fun main() {
    System.setProperty("javafx.animation.pulse", getRefreshRate().toString())
    launch(MCLauncher::class.java)
}