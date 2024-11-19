package net.unix.cloud.scheduler

import net.unix.api.scheduler.Scheduler
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.scheduler.SchedulerType

@DslMarker
private annotation class SchedulerMarker

/**
 * New instance of [Scheduler].
 *
 * @param init Scheduler params.
 *
 * @return Instance of [Scheduler].
 */
@SchedulerMarker
fun scheduler(type: SchedulerType = SchedulerType.COROUTINES, init: Scheduler.() -> Unit): Scheduler {
    val cloudScheduler = CloudSchedulerManager.create(type)
    cloudScheduler.init()

    return cloudScheduler
}

object CloudSchedulerManager : SchedulerManager {
    override fun create(type: SchedulerType): Scheduler {
        return when(type) {
            SchedulerType.COROUTINES -> CoroutineScheduler()
            SchedulerType.EXECUTOR -> ExecutorScheduler()
        }
    }
}