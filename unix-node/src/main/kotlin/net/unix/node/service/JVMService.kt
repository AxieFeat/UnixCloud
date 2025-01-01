package net.unix.node.service

import net.unix.api.LocationSpace
import net.unix.api.group.Group
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.*
import net.unix.api.service.exception.ServiceModificationException
import net.unix.api.service.wrapper.ServiceWrapper
import net.unix.node.CloudExtension.toJson
import net.unix.node.CloudExtension.uniqueUUID
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.service.ServiceDeleteEvent
import net.unix.node.logging.CloudLogger
import net.unix.node.mainDirectory
import net.unix.node.persistence.CloudPersistentDataContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*
import kotlin.jvm.Throws

/**
 * This class represents a very basic realisation of [StaticService] for JVM applications.
 */
@Suppress("LeakingThis")
open class JVMService(
    override val group: Group,
    override val uuid: UUID = uniqueUUID(),
    name: String,
    override var static: Boolean = false
) : StaticService, KoinComponent {

    private val serviceManager: ServiceManager by inject(named("default"))
    private val locationSpace: LocationSpace by inject(named("default"))

    override val clearName: String = name

    override val name: String
        get() {
            if (serviceManager.duplicates(clearName)) {
                return "$clearName ($uuid)"
            }

            return clearName
        }

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override val dataFolder: File = run {
        val file = File(locationSpace.service, uuid.toString())

        file.mkdirs()

        return@run file
    }
    override var status: ServiceStatus = ServiceStatus.PREPARED
        set(value) {
            field = value

            CloudLogger.info("Service $name marked as ${status.name.lowercase()}.")
        }

    override var wrapper: ServiceWrapper? = group.wrapper?.executableFor(this)

    override val uptime: Long
        get() {
            if(status != ServiceStatus.STARTED || status != ServiceStatus.STARTING) return 0

            return System.currentTimeMillis() - created
        }

    override val created: Long = System.currentTimeMillis()

    init {
        // Just copy files from template to work directory.
        val templates = group.templates

        templates.forEach { template ->
            val files = template.files

            files.forEach {
                val from = File(mainDirectory, it.from)
                val to = File(dataFolder, it.to)

                from.copyRecursively(to, overwrite = true)
            }
        }

        // Create info file for unix-driver
        val infoFile = File(dataFolder, "service.info")

        if (!infoFile.exists()) infoFile.createNewFile()

        this.serialize().toJson(infoFile)
    }

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun start(executable: ServiceWrapper, overwrite: Boolean) {
        if (status == ServiceStatus.DELETED) throw ServiceModificationException("You cannot run deleted CloudService!")
        if (status == ServiceStatus.STARTED || status == ServiceStatus.STARTING) throw IllegalArgumentException("CloudService already started!")

        val groupExecutable = group.wrapper

        if (groupExecutable == null || overwrite) {
            this.wrapper = executable
        }

        this.wrapper?.start()
    }

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun kill(delete: Boolean) {
        if (status == ServiceStatus.DELETED) throw ServiceModificationException("You cannot stop deleted CloudService!")

        wrapper?.kill()
        if(delete) delete()
    }

    @Throws(ServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
        if (status == ServiceStatus.DELETED) throw ServiceModificationException("CloudService already deleted!")
        if (status == ServiceStatus.STARTED || status == ServiceStatus.STARTING) throw IllegalArgumentException("Could not delete CloudService, at first stop it!")

        CloudLogger.info("Trying to delete $name...")

        ServiceDeleteEvent(this).callEvent()

        serviceManager.unregister(this)

        val templates = group.templates

        templates.forEach { template ->
            val files = template.backFiles

            files.forEach {
                val from = File(dataFolder, it.from)
                val to = File(mainDirectory, it.to)

                from.copyRecursively(to, overwrite = true)
            }
        }

        dataFolder.deleteRecursively()
    }

    override fun serialize(): Map<String, Any> {
        val serialized = mutableMapOf<String, Any>()

        serialized["uuid"] = uuid.toString()
        serialized["clearName"] = clearName
        serialized["group"] = group.uuid
        serialized["created"] = created
        serialized["data-folder"] = dataFolder.path
        serialized["status"] = status.name
        serialized["static"] = static

        return serialized
    }

    companion object {
        // Need for RMI server.
        @JvmStatic
        private val serialVersionUID = 8068719449091445131L
    }
}