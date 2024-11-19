package net.unix.cloud.service

import net.unix.api.scheduler.SchedulerType
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.service.CloudServiceStatus
import net.unix.cloud.scheduler.scheduler
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object BasicCloudServiceManager : CloudServiceManager {

    val cachedServices = mutableMapOf<UUID, CloudService>()

    override val services: Set<CloudService>
        get() = cachedServices.values.toSet()

    init {
        scheduler(SchedulerType.EXECUTOR) {
            execute(0, 1000) {
                services.forEach {
                   if (it.status == CloudServiceStatus.STARTED) {
                       it.uptime += 1
                   } else {
                       it.uptime = 0
                   }
                }
            }
        }
    }

    override fun register(service: CloudService) {
        cachedServices[service.uuid] = service
    }

    override fun unregister(service: CloudService) {
        cachedServices.remove(service.uuid)
    }

    override fun get(name: String): List<CloudService> = cachedServices.values.filter { it.clearName == name }

    override fun get(uuid: UUID): CloudService? = cachedServices[uuid]

}