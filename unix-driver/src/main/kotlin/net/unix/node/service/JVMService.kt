package net.unix.node.service

import net.unix.api.group.Group
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.ServiceStatus
import net.unix.api.service.StaticService
import net.unix.api.service.exception.ServiceModificationException
import net.unix.api.service.wrapper.ServiceWrapper
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

@Suppress("unused")
open class JVMService(
    override val group: Group,
    override val uuid: UUID = UUID.randomUUID(),
    name: String,
    override var static: Boolean = false
) : StaticService {

    override val clearName: String = name

    override val name: String
        get() {
            return clearName
        }
    override val persistentDataContainer: PersistentDataContainer = TODO()
    override val dataFolder: File = TODO()

    override var status: ServiceStatus = ServiceStatus.PREPARED
    override var wrapper: ServiceWrapper = TODO()

    override fun updateTemplate(): CompletableFuture<Unit> {
        TODO()
    }

    override val uptime: Long
        get() {
            if(status != ServiceStatus.STARTED || status != ServiceStatus.STARTING) return 0

            return System.currentTimeMillis() - created
        }
    override val created: Long = TODO()

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun start(executable: ServiceWrapper, overwrite: Boolean) {
    }

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun kill(delete: Boolean) {
    }

    override fun stop(delete: Boolean) {
        TODO("Not yet implemented")
    }

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
    }

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    override fun toString(): String {
        return "JVMService(persistentDataContainer=$persistentDataContainer, dataFolder=$dataFolder, status=$status, wrapper=$wrapper, created=$created, uptime=$uptime, name='$name', clearName='$clearName', static=$static, uuid=$uuid, group=$group)"
    }

    companion object {
        // Need for RMI server.
        @JvmStatic
        private val serialVersionUID = 8068719449091445131L
    }
}