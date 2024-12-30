package net.unix.api.service

import net.unix.api.pattern.Deletable
import net.unix.api.group.Group
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.exception.ServiceModificationException
import net.unix.api.service.wrapper.ServiceWrapper
import java.io.File
import java.rmi.RemoteException
import java.util.*

/**
 * [Service]'s allow to start instances of [Group].
 *
 * The service is some kind of executable program that is controlled by UnixCloud.
 * You can run almost anything using [ServiceWrapper].
 */
interface Service : ServiceInfo, PersistentDataHolder, Nameable, Deletable, RemoteAccessible {

    /**
     * Unique service id.
     */
    @get:Throws(RemoteException::class)
    override val uuid: UUID

    /**
     * Service name. Can be repeated by other services.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Name of service without any formatting.
     */
    @get:Throws(RemoteException::class)
    override val clearName: String

    /**
     * Service group.
     *
     * @throws ServiceModificationException If [status] is [ServiceStatus.DELETED].
     */
    @get:Throws(ServiceModificationException::class, RemoteException::class)
    override val group: Group

    /**
     * Time of creation this service in milliseconds.
     */
    @get:Throws(RemoteException::class)
    override val created: Long

    /**
     * Service folder.
     *
     * This is where all the runtime files of the service are stored.
     */
    @get:Throws(RemoteException::class)
    override val dataFolder: File

    /**
     * Service status.
     *
     * Change variable only if you KNOW, what a doing.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    override var status: ServiceStatus

    /**
     * Uptime of service in milliseconds.
     */
    @get:Throws(RemoteException::class)
    val uptime: Long

    /**
     * The service wrapper.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var wrapper: ServiceWrapper?

    /**
     * Start [Service] with some executable properties.
     *
     * @param executable Executable properties.
     * @param overwrite If true - [executable] will be used in any case, else [executable] may
     * be not used (If [Group] has some [Group.wrapper])
     *
     * @throws ServiceModificationException If [status] is [ServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [ServiceStatus.PREPARED].
     */
    @Throws(ServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    fun start(executable: ServiceWrapper, overwrite: Boolean = false)

    /**
     * Kill [Service].
     *
     * @param delete Delete [Service] files after stop?
     *
     * @throws [ServiceModificationException] If [status] is [ServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is not [ServiceStatus.STARTED].
     */
    @Throws(ServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    fun kill(delete: Boolean = true)

    /**
     * Delete [Service]. It also unregister via [ServiceManager.unregister]
     *
     * @throws ServiceModificationException If [status] is [ServiceStatus.DELETED].
     * @throws IllegalArgumentException If [status] is [ServiceStatus.STARTED].
     */
    @Throws(ServiceModificationException::class, IllegalArgumentException::class, RemoteException::class)
    override fun delete()

    companion object
}