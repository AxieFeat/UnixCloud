package net.unix.api.persistence

import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents the context in which the [PersistentDataType] can
 * serialize and deserialize the passed values.
 */
interface PersistentDataAdapterContext : RemoteAccessible {

    /**
     * Creates a new and empty meta container instance.
     *
     * @return the fresh container instance.
     */
    @Throws(RemoteException::class)
    fun newPersistentDataContainer(): PersistentDataContainer

    companion object

}