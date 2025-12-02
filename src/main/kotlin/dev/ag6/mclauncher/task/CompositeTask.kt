package dev.ag6.mclauncher.task

import javafx.beans.value.ChangeListener
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CompositeTask(
    name: String,
    private val parallel: Boolean,
    private val tasks: List<Task<*>>
) :
    Task<List<Any?>>(name) {
    override suspend fun run(): List<Any?> {
        return if (parallel) {
            runParallel()
        } else {
            runSequentially()
        }
    }

    private suspend fun runParallel(): List<Any?> = coroutineScope {
        tasks.forEach { subTask ->
            subTask.progressProperty.addListener { _, _, _ ->
                progress = tasks.sumOf { it.progressProperty.get().toDouble() }.toFloat() / tasks.size
            }
        }
        val results = tasks.map { async { it.execute() } }.map { it.await() }
        progress = 1.0f
        results
    }

    private suspend fun runSequentially(): List<Any?> {
        val results = mutableListOf<Any?>()
        tasks.forEachIndexed { index, task ->
            val baseProgress = index.toFloat() / tasks.size
            progress = baseProgress

            val listener = ChangeListener<Number> { _, _, newValue ->
                progress = baseProgress + (newValue.toFloat() / tasks.size)
            }
            task.progressProperty.addListener(listener)

            try {
                results.add(task.execute())
            } finally {
                task.progressProperty.removeListener(listener)
            }
        }
        progress = 1.0f
        return results
    }
}