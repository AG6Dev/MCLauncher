package dev.ag6.mclauncher.launch

import dev.ag6.mclauncher.instance.component.JavaComponent
import dev.ag6.mclauncher.instance.component.LaunchComponent
import dev.ag6.mclauncher.instance.component.MinecraftGameComponent
import java.io.File
import kotlin.io.path.pathString

class LaunchProcessBuilder(
    private val launchContext: InstanceLaunchContext
) {
    fun build(): Process {
        val instance = launchContext.instance
        val javaComponent = instance.getComponent<JavaComponent>()
            ?: throw IllegalStateException("Java component not found, cannot launch instance")
        instance.getComponent<MinecraftGameComponent>()
            ?: throw IllegalStateException("Minecraft game component not found, cannot launch instance")
        val launchComponents = instance.launchComponents

        val processBuilder = ProcessBuilder(javaComponent.javaPath)
        processBuilder.directory(instance.getMinecraftDirectory().toFile())

        processBuilder.command().add("-cp")
        processBuilder.command().add(buildClasspath(launchComponents))

        processBuilder.command().addAll(constructJvmArguments(launchComponents))
        processBuilder.command().add(instance.mainClassProvider.getMainClass(launchContext))
        processBuilder.command().addAll(constructGameArguments(launchComponents))

        return processBuilder.start()
    }

    private fun constructGameArguments(launchComponents: List<LaunchComponent>): List<String> {
        val gameArguments: MutableList<String> = mutableListOf()
        for (launchComponent in launchComponents) {
            launchComponent.addGameArgs(launchContext, gameArguments)
        }
        return gameArguments
    }

    private fun constructJvmArguments(launchComponents: List<LaunchComponent>): List<String> {
        val jvmArguments: MutableList<String> = mutableListOf()

        for (launchComponent in launchComponents) {
            launchComponent.addJvmArgs(launchContext, jvmArguments)
        }

        return jvmArguments
    }

    private fun buildClasspath(launchComponents: List<LaunchComponent>): String {
        val classPath: MutableList<String> = mutableListOf()

        for (component in launchComponents) {
            component.addToClasspath(launchContext, classPath)
        }

        val separator = File.pathSeparator
        classPath.addAll(launchContext.libraries.map { it.pathString })
        classPath.add(launchContext.clientJar.toString())
        return classPath.joinToString(separator) { it }.trimEnd(*separator.toCharArray())
    }
}