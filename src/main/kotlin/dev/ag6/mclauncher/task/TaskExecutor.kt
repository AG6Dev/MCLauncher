package dev.ag6.mclauncher.task

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class RunningTask<T>(val task: Task<T>, private val deferred: Deferred<T>) : Deferred<T> by deferred {
    override fun cancel() = deferred.cancel()
}

class TaskExecutor(maxConcurrent: Int = 4, dispatcher: CoroutineDispatcher = Dispatchers.Default) : AutoCloseable {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val semaphore = Semaphore(maxConcurrent)

    fun <T> submit(task: Task<T>): RunningTask<T> {
        val deferred = scope.async {
            semaphore.withPermit {
                try {
                    task.state = Task.State.RUNNING
                    task.execute().also {
                        task.state = if (isActive) Task.State.COMPLETED else Task.State.CANCELLED
                    }
                } catch (e: CancellationException) {
                    task.state = Task.State.CANCELLED
                    throw e
                } catch (e: Exception) {
                    task.state = Task.State.FAILED
                    throw e
                }
            }
        }
        return RunningTask(task, deferred)
    }

    override fun close() {
        scope.cancel()
    }
}