package net.unix.api.service

import net.unix.api.pattern.Deletable
import net.unix.api.group.CloudGroup
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.exception.CloudServiceModificationException
import java.io.File
import java.rmi.RemoteException
import java.util.*

/**
 * [CloudService]'s allow to start instances of [CloudGroup].
 *
 * The service is some kind of executable program that is controlled by UnixCloud.
 * You can run almost anything using [CloudServiceExecutable].
 */
interface CloudService : PersistentDataHolder, Nameable, Deletable, RemoteAccessible {

    /**
     * Unique service id.
     */
    @get:Throws(RemoteException::class)
    val uuid: UUID

    /**
     * Service name. Can be repeated by other services.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Name of service without any formatting.
     */
    @get:Throws(RemoteException::class)
    val clearName: String

    /**
     * Service group.
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     */
    @get:Throws(CloudServiceModificationException::class, RemoteException::class)
    val group: CloudGroup

    /**
     * Time of creation this service in milliseconds.
     */
    @get:Throws(RemoteException::class)
    val created: Long

    /**
     * Service folder.
     *
     * This is where all the runtime files of the service are stored.
     */
    @get:Throws(RemoteException::class)
    val dataFolder: File

    /**
     * Service status.
     *
     * Change variable only if you KNOW, what a doing.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var status: CloudServiceStatus

    /**
     * Uptime of service in seconds.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var uptime: Long

    /**
     * The service executable.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var executable: CloudServiceExecutable?

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
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    fun start(executable: CloudServiceExecutable, overwrite: Boolean = false)

    /**
     * Kill [CloudService].
     *
     * @param delete Delete [CloudService] files after stop?
     *
     * @throws [CloudServiceModificationException] If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    fun kill(delete: Boolean = true)

    /**
     * Delete [CloudService]. It also unregister via [CloudServiceManager.unregister]
     *
     * @throws CloudServiceModificationException If [status] is [CloudServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is [CloudServiceStatus.STARTED].
     */
    @Throws(CloudServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    override fun delete()

    companion object
}