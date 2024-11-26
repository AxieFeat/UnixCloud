package net.unix.api.service

import net.unix.api.pattern.Deletable
import net.unix.api.group.CloudGroup
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.service.exception.CloudServiceModificationException
import java.io.File
import java.util.*

/**
 * [CloudService]'s allow to start instances of [CloudGroup].
 *
 * The service is some kind of executable program that is controlled by UnixCloud.
 * You can run almost anything using [ServiceExecutable].
 */
interface CloudService : PersistentDataHolder, Nameable, Deletable {

    /**
     * Unique service id.
     */
    val uuid: UUID

    /**
     * Service name. Can be repeated by other services.
     */
    override val name: String

    /**
     * Name of service without any formatting.
     */
    val clearName: String

    /**
     * Service group.
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     */
    @get:Throws(CloudServiceModificationException::class)
    val group: CloudGroup

    /**
     * Time of creation this service in milliseconds.
     */
    val created: Long

    /**
     * Service folder.
     *
     * This is where all the runtime files of the service are stored.
     */
    val dataFolder: File

    /**
     * Service status.
     *
     * Change variable only if you KNOW, what a doing.
     */
    var status: CloudServiceStatus

    /**
     * Uptime of service in seconds.
     */
    var uptime: Long

    /**
     * The service executable.
     */
    var executable: ServiceExecutable?

    /**
     * Start [CloudService] with some executable properties.
     *
     * @param executable Executable properties.
     * @param overwrite If true - [executable] will be used in any case, else [executable] may
     * be not used (If [CloudGroup] has some [CloudGroup.groupExecutable])
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [CloudServiceStatus.PREPARED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    fun start(executable: ServiceExecutable, overwrite: Boolean = false)

    /**
     * Kill [CloudService].
     *
     * @param delete Delete [CloudService] files after stop?
     *
     * @throws [CloudServiceModificationException] If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    fun kill(delete: Boolean = true)

    /**
     * Delete [CloudService]. It also unregister via [CloudServiceManager.unregister]
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    override fun delete()

    companion object
}