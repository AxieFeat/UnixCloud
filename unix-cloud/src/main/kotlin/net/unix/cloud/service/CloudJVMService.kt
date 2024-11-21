package net.unix.cloud.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.ServiceExecutable
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.StaticCloudService
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.cloud.CloudExtension.copyFile
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.CloudInstance
import net.unix.cloud.logging.CloudLogger
import net.unix.cloud.mainDirectory
import net.unix.cloud.persistence.CloudPersistentDataContainer
import java.io.File
import java.util.*
import kotlin.jvm.Throws

@Suppress("LeakingThis")
open class CloudJVMService(
    override val group: CloudGroup,
    override val uuid: UUID = uniqueUUID(),
    name: String,
    override var static: Boolean = false
) : StaticCloudService {

    override val clearName: String = name

    override val name: String
        get() {
            val any = CloudInstance.instance.cloudServiceManager[clearName].any { it != this }

            if (any) {
                return "$clearName ($uuid)"
            }

            return clearName
        }

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override val dataFolder: File = run {
        val file = File(CloudInstance.instance.locationSpace.service, uuid.toString())

        file.mkdirs()

        return@run file
    }
    override var status: CloudServiceStatus = CloudServiceStatus.PREPARED

    override var executable: ServiceExecutable? = group.groupExecutable?.executableFor(this)

    override var uptime: Long = 0
    override val created: Long = System.currentTimeMillis()

    init {
        val templates = group.templates

        templates.forEach { template ->
            val files = template.files

            files.forEach {
                copyFile(
                    File(mainDirectory, it.from.toString()),
                    File(dataFolder, it.to.toString())
                )
            }
        }
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun start(executable: ServiceExecutable, overwrite: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot run deleted CloudService!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("CloudService already started!")

        val groupExecutable = group.groupExecutable

        if (groupExecutable == null || overwrite) {
            this.executable = executable
        }

        this.executable?.start()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun kill(delete: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot stop deleted CloudService!")

        executable?.kill()
        if(delete) delete()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("CloudService already deleted!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("Could not delete CloudService, at first stop it!")

        CloudLogger.info("Trying to delete $name...")

        CloudInstance.instance.cloudServiceManager.unregister(this)

        val templates = group.templates

        templates.forEach { template ->
            val files = template.backFiles

            files.forEach {
                copyFile(
                    File(dataFolder, it.from.toString()),
                    File(mainDirectory, it.to.toString())
                )
            }
        }

        dataFolder.deleteRecursively()
    }

    companion object
}