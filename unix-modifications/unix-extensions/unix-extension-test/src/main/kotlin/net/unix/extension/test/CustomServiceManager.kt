@file:Suppress("unused")

package net.unix.extension.test

import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.node.logging.CloudLogger
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object CustomServiceManager : ServiceManager  {

    private fun readResolve(): Any = CustomServiceManager

    val cachedServices = mutableMapOf<UUID, Service>()

    override val services: Set<Service>
        get() = cachedServices.values.toSet()

    override fun register(value: Service) {
        cachedServices[value.uuid] = value
        CloudLogger.info("Registered ${value.name} service!")
    }

    override fun unregister(value: Service) {
        cachedServices.remove(value.uuid)
    }

    override fun get(name: String): List<Service> = cachedServices.values.filter { it.clearName == name }

    override fun get(uuid: UUID): Service? = cachedServices[uuid]

}