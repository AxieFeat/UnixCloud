package net.unix.api.group

import net.unix.api.pattern.Serializable
import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceWrapper
import java.rmi.RemoteException

/**
 * Allows to create CloudGroup type.
 * If you set type of group - all services will be started with this executable properties.
 *
 * Use singleton for creation own types. (Object class in kotlin)
 *
 * @throws IllegalArgumentException If type with this name already exist.
 */
interface GroupWrapper : Serializable, Nameable, RemoteAccessible {

    /**
     * The name of executable.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Create executable for service.
     *
     * @param service Service.
     *
     * @return Instance of [CloudServiceWrapper].
     */
    @Throws(RemoteException::class)
    fun executableFor(service: CloudService): CloudServiceWrapper

}