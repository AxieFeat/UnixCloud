package net.unix.api.scheduler

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Dsl marker
 */
@DslMarker
annotation class SimpleDsl3

/**
 * New instance of [CloudScheduler]
 *
 * @param param Scheduler params
 *
 * @return Instance of [CloudScheduler]
 */
@SimpleDsl3
fun scheduler(param: CloudScheduler.() -> Unit): CloudScheduler {
    val cloudScheduler = CloudScheduler()
    param.invoke(cloudScheduler)
    return cloudScheduler
}

class CloudScheduler {

    companion object {
        private val executorService = Executors.newScheduledThreadPool(5)
    }

    /**
     * Delay executing
     */
    var delay = -1L

    /**
     * Period executing
     */
    var period = -1L

    /**
     * Time unit
     */
    var unit = TimeUnit.MILLISECONDS

    /**
     * Run task in scheduler
     *
     * @param delay Delay executing
     * @param period Period executing
     * @param unit Time unit
     * @param task Task
     */
    fun CloudScheduler.execute(delay: Long = this.delay, period: Long = this.period, unit: TimeUnit = this.unit, task: Callable<Unit>) {
        val runnable = Runnable { task.call() }

        if (delay != -1L && period != -1L) {
            executorService.scheduleAtFixedRate(runnable, delay, period, unit)
            return
        }

        if (delay != -1L) {
            executorService.schedule(runnable, delay, unit)
            return
        }

        executorService.execute(runnable)
    }
}