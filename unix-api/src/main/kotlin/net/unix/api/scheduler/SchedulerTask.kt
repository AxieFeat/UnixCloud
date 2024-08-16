package net.unix.api.scheduler

interface SchedulerTask {

    /**
     * Task id
     */
    val id: Int

    /**
     * The scheduler that created this task.
     *
     * @see Scheduler
     */
    val scheduler: Scheduler

    /**
     * Cancels the task.
     *
     * If the task has already been cancelled, the method does nothing.
     *
     * @see [cancelled]
     */
    fun cancel()

    /**
     * Is task has been cancelled.
     */
    val cancelled: Boolean

    companion object
}