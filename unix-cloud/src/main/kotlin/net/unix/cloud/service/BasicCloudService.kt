package net.unix.cloud.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.service.CloudExecutable
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.cloud.CloudInstance
import net.unix.cloud.persistence.CloudPersistentDataContainer
import java.io.File
import java.util.*
import kotlin.jvm.Throws

open class BasicCloudService(
    override val group: CloudGroup,
    override var name: String,
    override val uuid: UUID = uniqueUUID()
) : CloudService {

    private lateinit var executable: CloudExecutable

    override val persistentDataContainer: PersistentDataContainer = CloudPersistentDataContainer()

    override val dataFolder: File = TODO()
    override var status: CloudServiceStatus = CloudServiceStatus.PREPARED

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun start(executable: CloudExecutable, overwrite: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot run deleted CloudService!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("CloudService already started!")

        if (executable.service != this) {
            val copy = executable.copy()

            this.executable = executable

            copy.start()
        } else {
            this.executable = executable

            executable.start()
        }
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun stop(delete: Boolean) {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("You cannot stop deleted CloudService!")

        executable.stop()
    }

    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun delete() {
        if (status == CloudServiceStatus.DELETED) throw CloudServiceModificationException("CloudService already deleted!")
        if (status == CloudServiceStatus.STARTED) throw IllegalArgumentException("Could not delete CloudService, at first stop it!")

        dataFolder.deleteRecursively()
    }

    companion object {

        private val current
            get() = CloudInstance.instance.cloudServiceManager.services.map { it.uuid }

        /**
         * Generate unique UUID for [CloudService]. It'll be unique by current session.
         *
         * @return Unique UUID.
         */
        fun uniqueUUID(): UUID {
            val random = UUID.randomUUID()

            if (current.contains(random)) return uniqueUUID()

            return random
        }
    }
}