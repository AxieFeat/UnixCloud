package net.unix.api.scheduler

import net.unix.api.CloudAPI

@DslMarker
private annotation class SchedulerMarker1

/**
 * New instance of [Scheduler]
 *
 * @param init Scheduler params
 *
 * @return Instance of [Scheduler]
 */
@SchedulerMarker1
fun scheduler(type: SchedulerType = SchedulerType.COROUTINES, init: Scheduler.() -> Unit): Scheduler {
    val cloudScheduler = CloudAPI.instance.schedulerManager.create(type)
    cloudScheduler.init()

    return cloudScheduler
}

/**
 * Class for manage schedulers
 *
 * For INTERNAL usage only!
 */
interface SchedulerManager {

    /**
     * Create instance of [Scheduler]
     *
     * For INTERNAL usage only!
     *
     * @param type Scheduler type
     *
     * @return Instance of [Scheduler]
     */
    fun create(type: SchedulerType): Scheduler

    companion object
}