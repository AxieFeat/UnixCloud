package net.unix.api.group.wrapper

import net.unix.api.pattern.Nameable
import net.unix.api.pattern.Serializable
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.Service
import net.unix.api.service.wrapper.ServiceWrapper
import net.unix.api.group.Group
import java.rmi.RemoteException

/**
 * Allows to create wrapper of [Group].
 * If you set wrapper of group - all services will be started with this executable properties.
 */
interface GroupWrapper : RemoteAccessible, Serializable, Nameable {

    /**
     * The unique name of wrapper.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Path to executable file in prepared service. It will be started via [ServiceWrapper].
     */
    @get:Throws(RemoteException::class)
    val executableFile: String

    /**
     * Create executable for service.
     *
     * @param service Service.
     *
     * @return Instance of [ServiceWrapper].
     */
    @Throws(RemoteException::class)
    fun wrapperFor(service: Service): ServiceWrapper

}