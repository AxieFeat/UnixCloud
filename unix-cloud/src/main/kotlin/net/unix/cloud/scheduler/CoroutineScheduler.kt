package net.unix.cloud.scheduler

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import net.unix.api.scheduler.Scheduler
import net.unix.api.scheduler.SchedulerTask
import net.unix.api.terminal.logger.Logger

open class CoroutineScheduler(
    private val logger: Logger
) : AbstractScheduler() {

    private val scope = CoroutineScope(Dispatchers.Default)

    override var delay: Long = -1L
    override var period: Long = -1L

    override fun Scheduler.execute(delay: Long, period: Long, task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()

        if (delay != -1L && period != -1L) {
            val flow = flow {
                delay(delay)
                emit(Unit)
                while (true) {
                    delay(period)
                    emit(Unit)
                }
            }
                .onEach {
                    scope.launch {
                        try {
                            task()
                        } catch (throwable: Throwable) {
                            logger.error("", throwable = throwable)
                        }
                    }
                }
                .onCompletion { unregisterTask(taskId) }
                .launchIn(scope)

            return newTask(taskId, flow)
        }

        if (delay != -1L) {
            val job = scope.launch {
                delay(delay)
                try {
                    task()
                } catch (throwable: Throwable) {
                    logger.error("", throwable = throwable)
                }
            }.also {
                it.invokeOnCompletion {
                    unregisterTask(taskId)
                }
            }

            return newTask(taskId, job)
        }

        val job = scope.launch {
            try {
                task()
            } catch (throwable: Throwable) {
                logger.error("", throwable = throwable)
            }
            unregisterTask(taskId)
        }

        return newTask(taskId, job)
    }

    private fun newTask(id: Int, job: Job): SchedulerTask {
        val task = object : SchedulerTask {
            override val scheduler: Scheduler = this@CoroutineScheduler

            override fun cancel() {
                try {
                    job.cancel()
                } finally {
                    unregisterTask(id)
                }
            }

            override val cancelled: Boolean
                get() = job.isCancelled

            override val id: Int = id
        }

        registerTask(task)
        return task
    }


}