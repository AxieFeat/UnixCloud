@file:Suppress("unused")

package net.unix.extension.test

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.node.logging.CloudLogger
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object CustomServiceManager : CloudServiceManager  {

    private fun readResolve(): Any = CustomServiceManager

    val cachedServices = mutableMapOf<UUID, CloudService>()

    override val services: Set<CloudService>
        get() = cachedServices.values.toSet()

    override fun register(service: CloudService) {
        cachedServices[service.uuid] = service
        CloudLogger.info("Registered ${service.name} service!")
    }

    override fun unregister(service: CloudService) {
        cachedServices.remove(service.uuid)
    }

    override fun get(name: String): List<CloudService> = cachedServices.values.filter { it.clearName == name }

    override fun get(uuid: UUID): CloudService? = cachedServices[uuid]

}