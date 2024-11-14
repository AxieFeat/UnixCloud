package net.unix.cloud.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object BasicCloudServiceManager : CloudServiceManager {

    val cachedServices = mutableMapOf<UUID, CloudService>()

    override val services: Set<CloudService>
        get() = cachedServices.values.toSet()

    override fun register(service: CloudService) {
        cachedServices[service.uuid] = service
    }

    override fun get(name: String): List<CloudService> = cachedServices.values.filter { it.name == name }

    override fun get(uuid: UUID): CloudService? = cachedServices[uuid]

}