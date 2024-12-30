@file:Suppress("unused")

package net.unix.api.service

import net.unix.api.group.Group
import net.unix.api.pattern.manager.UUIDManager
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import java.util.UUID

/**
 * Manager for [Service]'s.
 *
 * With this you can control the running [Service]'s.
 * To start a new service, use [Group.create]
 */
interface ServiceManager : UUIDManager<Service>, RemoteAccessible {

    /**
     * Set of current [Service]'s.
     */
    @get:Throws(RemoteException::class)
    val services: Set<Service>

    /**
     * Register service in [services].
     *
     * @param value Service to register.
     */
    @Throws(RemoteException::class)
    override fun register(value: Service)

    /**
     * Unregister service from [services].
     *
     * @param value Service to unregister.
     */
    @Throws(RemoteException::class)
    override fun unregister(value: Service)

    /**
     * Is exist [Service]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [Service]'s with this name > 1, else false
     */
    @Throws(RemoteException::class)
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [Service] by [name].
     *
     * @param name Service name.
     *
     * @return List of [Service]'s with this name, can be empty.
     */
    @Throws(RemoteException::class)
    override fun get(name: String): List<Service>

    /**
     * Get [Service] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [Service] or null, if not founded.
     */
    @Throws(RemoteException::class)
    override fun get(uuid: UUID): Service?

    companion object
}