package net.unix.api.pattern.manager

import java.rmi.RemoteException

/**
 * This interface represents a some manager.
 *
 * @param T Type of object to manage.
 */
interface Manager<T> {

    /**
     * Register new instance.
     *
     * @param value Value to register.
     */
    @Throws(RemoteException::class)
    fun register(value: T)

    /**
     * Unregister some instance.
     *
     * @param value Value to unregister.
     */
    @Throws(RemoteException::class)
    fun unregister(value: T)

    /**
     * Get some instance by [name].
     *
     * @param name Name of instance.
     *
     * @return Some object instance or null, if not found.
     */
    @Throws(RemoteException::class)
    operator fun get(name: String): Any?

}