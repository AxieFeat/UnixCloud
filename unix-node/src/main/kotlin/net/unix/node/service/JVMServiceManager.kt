package net.unix.node.service

import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object JVMServiceManager : ServiceManager {

    private fun readResolve(): Any = JVMServiceManager

    val cachedServices = mutableMapOf<UUID, Service>()

    override val services: Set<Service>
        get() = cachedServices.values.toSet()

    override fun register(value: Service) {
        cachedServices[value.uuid] = value
    }

    override fun unregister(value: Service) {
        cachedServices.remove(value.uuid)
    }

    override fun get(name: String): List<Service> = cachedServices.values.filter { it.clearName == name }

    override fun get(uuid: UUID): Service? = cachedServices[uuid]

}