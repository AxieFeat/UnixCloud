package net.unix.scheduler.impl

import net.unix.scheduler.Scheduler
import net.unix.scheduler.SchedulerManager
import net.unix.scheduler.SchedulerType

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
    val cloudScheduler = BasicSchedulerManager.create(type)
    cloudScheduler.init()

    return cloudScheduler
}

object BasicSchedulerManager : SchedulerManager {
    override fun create(type: SchedulerType): Scheduler {
        return when(type) {
            SchedulerType.COROUTINES -> CoroutineScheduler()
            SchedulerType.EXECUTOR -> ExecutorScheduler()
        }
    }
}