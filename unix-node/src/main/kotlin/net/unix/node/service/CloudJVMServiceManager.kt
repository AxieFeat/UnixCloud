package net.unix.node.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object CloudJVMServiceManager : CloudServiceManager {

    private fun readResolve(): Any = CloudJVMServiceManager

    val cachedServices = mutableMapOf<UUID, CloudService>()

    override val services: Set<CloudService>
        get() = cachedServices.values.toSet()

    override fun register(service: CloudService) {
        cachedServices[service.uuid] = service
    }

    override fun unregister(service: CloudService) {
        cachedServices.remove(service.uuid)
    }

    override fun get(name: String): List<CloudService> = cachedServices.values.filter { it.clearName == name }

    override fun get(uuid: UUID): CloudService? = cachedServices[uuid]

}