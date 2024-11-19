package net.unix.cloud.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudExecutable
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.StaticCloudService
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.cloud.CloudExtension.copyFile
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.CloudInstance
import net.unix.cloud.mainDirectory
import net.unix.cloud.persistence.CloudPersistentDataContainer
import java.io.File
import java.util.*
import kotlin.jvm.Throws

@Suppress("LeakingThis")
open class BasicCloudService(
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

    override lateinit var executable: CloudExecutable

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override val dataFolder: File = File(CloudInstance.instance.locationSpace.service, uuid.toString())
    override var status: CloudServiceStatus = CloudServiceStatus.PREPARED

    override var uptime: Long = 0
    override val created: Long = System.currentTimeMillis()

    init {
        val templates = group.templates

        if(!dataFolder.exists()) dataFolder.mkdirs()

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
    override fun start(executable: CloudExecutable, overwrite: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot run deleted CloudService!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("CloudService already started!")

        val groupExecutable = group.type

        if (groupExecutable != null && !overwrite) {
            this.executable = groupExecutable
        } else {
            this.executable = executable
        }

        this.executable.start()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun stop(delete: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot stop deleted CloudService!")

        executable.kill()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("CloudService already deleted!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("Could not delete CloudService, at first stop it!")

        CloudInstance.instance.cloudServiceManager.unregister(this)
        dataFolder.deleteRecursively()
    }

    companion object
}