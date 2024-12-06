@file:Suppress("unused")

package net.unix.api.service

import net.unix.api.group.CloudGroup
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import java.util.UUID

/**
 * Manager for [CloudService]'s.
 *
 * With this you can control the running [CloudService]'s.
 * To start a new service, use [CloudGroup.create]
 */
interface CloudServiceManager : RemoteAccessible {

    /**
     * Set of current [CloudService]'s.
     */
    @get:Throws(RemoteException::class)
    val services: Set<CloudService>

    /**
     * Register service in [services].
     *
     * @param service Service to register.
     */
    @Throws(RemoteException::class)
    fun register(service: CloudService)

    /**
     * Unregister service from [services].
     *
     * @param service Service to unregister.
     */
    @Throws(RemoteException::class)
    fun unregister(service: CloudService)

    /**
     * Is exist [CloudService]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [CloudService]'s with this name > 1, else false
     */
    @Throws(RemoteException::class)
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [CloudService] by name.
     *
     * @param name Service name.
     *
     * @return List of [CloudService]'s with this name, can be empty.
     */
    @Throws(RemoteException::class)
    operator fun get(name: String): List<CloudService>

    /**
     * Get [CloudService] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [CloudService] or null, if not founded.
     */
    @Throws(RemoteException::class)
    operator fun get(uuid: UUID): CloudService?

    companion object
}