package net.unix.api.service

import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.service.exception.CloudServiceModificationException
import java.io.File
import java.util.*

/**
 * [CloudService]'s allow to start instances of [CloudGroup].
 */
interface CloudService : PersistentDataHolder {

    /**
     * Unique service id.
     */
    val uuid: UUID

    /**
     * Service name. Can be repeated by other services.
     */
    var name: String

    /**
     * Service group.
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     */
    @get:Throws(CloudServiceModificationException::class)
    val group: CloudGroup

    /**
     * Service folder.
     */
    val dataFolder: File

    /**
     * Service status.
     *
     * Change variable only if you KNOW, what a doing.
     */
    var status: CloudServiceStatus

    /**
     * Start [CloudService] with some executable properties.
     *
     * @param executable Executable properties.
     * @param overwrite If true - [executable] will be used in any case, else [executable] may
     * be not used (If [CloudGroup] has some [CloudGroup.type])
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [CloudServiceStatus.PREPARED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    fun start(executable: CloudExecutable, overwrite: Boolean = false)

    /**
     * Stop [CloudService].
     *
     * @param delete Delete [CloudService] files after stop?
     *
     * @throws [CloudServiceModificationException] If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    fun stop(delete: Boolean = true)

    /**
     * Delete [CloudService].
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class)
    fun delete()

    companion object
}