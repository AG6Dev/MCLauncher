package dev.ag6.mclauncher.task

import javafx.beans.property.FloatProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

abstract class Task<out T>(val name: String) {
    val progressProperty: FloatProperty = SimpleFloatProperty(0.0f)
    val stateProperty: ObjectProperty<State> = SimpleObjectProperty(State.PENDING)

    protected var progress: Float
        get() = progressProperty.get()
        set(value) = progressProperty.set(value)

    internal var state: State
        get() = stateProperty.get()
        set(value) = stateProperty.set(value)

    suspend fun execute(): T {
        currentCoroutineContext().ensureActive()
        return run()
    }

    protected abstract suspend fun run(): T

    enum class State {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}