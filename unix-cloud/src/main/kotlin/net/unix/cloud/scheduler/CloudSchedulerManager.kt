package net.unix.cloud.scheduler

import net.unix.api.scheduler.Scheduler
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.scheduler.SchedulerType
import net.unix.api.terminal.logger.Logger
import net.unix.cloud.CloudInstance

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
    val cloudScheduler = CloudInstance.instance.schedulerManager.create(type)
    cloudScheduler.init()

    return cloudScheduler
}

class CloudSchedulerManager(
    private val logger: Logger
) : SchedulerManager {
    override fun create(type: SchedulerType): Scheduler {
        return when(type) {
            SchedulerType.COROUTINES -> CoroutineScheduler(logger)
            SchedulerType.EXECUTOR -> ExecutorScheduler()
        }
    }
}