package net.unix.api.scheduler

/**
 * Class for manage schedulers.
 *
 * For INTERNAL usage only!
 */
interface SchedulerManager {

    /**
     * Create instance of [Scheduler]
     *
     * For INTERNAL usage only!
     *
     * @param type Scheduler type.
     *
     * @return Instance of [Scheduler]
     */
    fun create(type: SchedulerType): Scheduler

    companion object
}