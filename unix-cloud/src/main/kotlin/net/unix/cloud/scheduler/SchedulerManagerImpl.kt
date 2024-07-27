package net.unix.cloud.scheduler

import net.unix.api.scheduler.Scheduler
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.scheduler.SchedulerType

class SchedulerManagerImpl : SchedulerManager {
    override fun create(type: SchedulerType): Scheduler {
        return when(type) {
            SchedulerType.COROUTINES -> CoroutineScheduler()
            SchedulerType.EXECUTOR -> ExecutorScheduler()
        }
    }
}