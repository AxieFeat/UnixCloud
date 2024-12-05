@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.node.bridge

import net.unix.api.bridge.CloudBridge
import net.unix.api.network.server.Server
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.CloudServiceManager
import net.unix.node.NamespacedKey
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object JVMBridge : CloudBridge, KoinComponent {

    private fun readResolve(): Any = JVMBridge

    private val serviceManager: CloudServiceManager by inject()

    private val cachedClients = mutableMapOf<Int, UUID>()
    override val clients: Map<Int, UUID>
        get() = cachedClients

    val serviceMaxMemory = NamespacedKey.unix("service.max-memory")
    val serviceUsedMemory = NamespacedKey.unix("service.used-memory")
    val serviceFreeMemory = NamespacedKey.unix("service.free-memory")

    override fun configure(server: Server) {
        server.createListener("fun:service:auth") { conn, packet ->
            if(packet == null) return@createListener

            val id = conn.id
            val uuid = UUID.fromString(packet["uuid"])

            cachedClients[id] = uuid

            val service = serviceManager[uuid] ?: run {
                cachedClients.remove(id)
                return@createListener
            }

            CloudLogger.info("Service ${service.name} started!")
        }

        server.createListener("fun:service:memory:max") { conn, packet ->
            if(packet == null) return@createListener

            val id = conn.id
            val service = serviceManager[
                cachedClients[id] ?: return@createListener
            ] ?: run {
                cachedClients.remove(id)
                return@createListener
            }
            val memory: Long = packet["memory"] ?: 0L

            service.persistentDataContainer[serviceMaxMemory, PersistentDataType.LONG] = memory
        }

        server.createListener("fun:service:memory:used") { conn, packet ->
            if(packet == null) return@createListener

            val id = conn.id
            val service = serviceManager[
                cachedClients[id] ?: return@createListener
            ] ?: run {
                cachedClients.remove(id)
                return@createListener
            }
            val memory: Long = packet["memory"] ?: 0L

            service.persistentDataContainer[serviceUsedMemory, PersistentDataType.LONG] = memory
        }

        server.createListener("fun:service:memory:free") { conn, packet ->
            if(packet == null) return@createListener

            val id = conn.id
            val service = serviceManager[
                cachedClients[id] ?: return@createListener
            ] ?: run {
                cachedClients.remove(id)
                return@createListener
            }
            val memory: Long = packet["memory"] ?: 0L

            service.persistentDataContainer[serviceFreeMemory, PersistentDataType.LONG] = memory
        }
        CloudLogger.info("Registered JVMBridge in ${server.port} TCP/IP")
    }
}