package net.unix.node.service

import net.unix.api.LocationSpace
import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.*
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.api.service.wrapper.CloudServiceWrapper
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

@Suppress("LeakingThis")
open class CloudJVMService(
    override val group: CloudGroup,
    override val uuid: UUID = uniqueUUID(),
    name: String,
    override var static: Boolean = false
) : StaticCloudService, KoinComponent {

    private val cloudServiceManager: CloudServiceManager by inject(named("default"))
    private val locationSpace: LocationSpace by inject(named("default"))

    override val clearName: String = name

    override val name: String
        get() {
            if (cloudServiceManager.duplicates(clearName)) {
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
    override var status: CloudServiceStatus = CloudServiceStatus.PREPARED

    override var wrapper: CloudServiceWrapper? = group.groupWrapper?.executableFor(this)

    override val uptime: Long
        get() {
            if(status != CloudServiceStatus.STARTED) return 0

            return System.currentTimeMillis() - created
        }

    override val created: Long = System.currentTimeMillis()

    init {
        val templates = group.templates

        templates.forEach { template ->
            val files = template.files

            files.forEach {
                val from = File(mainDirectory, it.from)
                val to = File(dataFolder, it.to)

                from.copyRecursively(to, overwrite = true)
            }
        }

        val infoFile = File(dataFolder, "service.info")

        if (!infoFile.exists()) infoFile.createNewFile()

        this.serialize().toJson(infoFile)
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun start(executable: CloudServiceWrapper, overwrite: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot run deleted CloudService!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("CloudService already started!")

        val groupExecutable = group.groupWrapper

        if (groupExecutable == null || overwrite) {
            this.wrapper = executable
        }

        this.wrapper?.start()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun kill(delete: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot stop deleted CloudService!")

        wrapper?.kill()
        if(delete) delete()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("CloudService already deleted!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("Could not delete CloudService, at first stop it!")

        CloudLogger.info("Trying to delete $name...")

        ServiceDeleteEvent(this).callEvent()

        cloudServiceManager.unregister(this)

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
        @JvmStatic
        private val serialVersionUID = 8068719449091445131L
    }
}