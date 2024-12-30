package net.unix.api.pattern.manager

import java.rmi.RemoteException
import java.util.UUID

/**
 * This interface represents a manager with [UUID] getter.
 */
interface UUIDManager<T> : Manager<T> {

    /**
     * Get some instance by [uuid].
     *
     * @param uuid Uuid of instance.
     *
     * @return Some object instance or null, if not found.
     */
    @Throws(RemoteException::class)
    operator fun get(uuid: UUID): Any?

}