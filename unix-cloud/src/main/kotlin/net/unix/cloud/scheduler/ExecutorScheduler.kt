package net.unix.cloud.scheduler

import kotlinx.coroutines.runBlocking
import net.unix.api.scheduler.Scheduler
import net.unix.api.scheduler.SchedulerTask
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ExecutorScheduler : AbstractScheduler() {

    private val executor = Executors.newScheduledThreadPool(8)

    override var delay: Long = -1L
    override var period: Long = -1L

    override fun Scheduler.execute(delay: Long, period: Long, task: suspend () -> Unit): SchedulerTask {
        val taskId = nextId()

        if (delay != -1L && period != -1L) {
            val wrapped = Runnable {
                try {
                    runBlocking {
                        task.invoke()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    unregisterTask(taskId)
                }
            }

            val future = executor.scheduleAtFixedRate(wrapped, delay, period, TimeUnit.MILLISECONDS)

            return newTask(taskId, future)
        }

        if (delay != -1L) {
            val wrapped = Runnable {
                try {
                    runBlocking {
                        task.invoke()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                } finally {
                    unregisterTask(taskId)
                }
            }

            val future = executor.schedule(wrapped, delay, TimeUnit.MILLISECONDS)

            return newTask(taskId, future)
        }

        val wrapped = Runnable {
            try {
                runBlocking {
                    task.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                unregisterTask(taskId)
            }
        }

        val future = executor.submit(wrapped)

        return newTask(taskId, future)
    }

    private fun newTask(id: Int, future: Future<*>): SchedulerTask {
        val task = object : SchedulerTask {
            override val scheduler: ExecutorScheduler = this@ExecutorScheduler
            override val id: Int = id

            override fun cancel() {
                future.cancel(false)
                unregisterTask(id)
            }

            override val cancelled: Boolean
                get() = future.isCancelled
        }
        registerTask(task)
        return task
    }

}