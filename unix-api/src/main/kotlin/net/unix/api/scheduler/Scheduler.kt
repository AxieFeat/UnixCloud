package net.unix.api.scheduler

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@DslMarker
annotation class SimpleDsl3

object Scheduler {

    @SimpleDsl3
    fun scheduler(param: CloudScheduler.() -> Unit): CloudScheduler {
        val cloudScheduler = CloudScheduler()
        param.invoke(cloudScheduler)
        return cloudScheduler
    }
}

class CloudScheduler {

    companion object {
        private val executorService = Executors.newScheduledThreadPool(5)
    }

    var delay = -1L
    var period = -1L
    var unit = TimeUnit.MILLISECONDS

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