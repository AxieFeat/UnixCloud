package net.unix.api.modification

import net.unix.api.remote.RemoteAccessible
import java.io.File
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * General interface for UnixCloud modifications.
 */
interface Modification : RemoteAccessible {

    /**
     * Modification folder location.
     */
    @get:Throws(RemoteException::class)
    var folder: File

    /**
     * Modification executable file location.
     */
    @get:Throws(RemoteException::class)
    var executable: File

    /**
     * Modification information
     */
    @get:Throws(RemoteException::class)
    var modification: ModificationInfo

    /**
     * Call when modification loaded.
     */
    @Throws(RemoteException::class)
    fun onLoad()

    /**
     * Register listener for modification.
     *
     * @param listener Listener instance.
     */
    @Throws(RemoteException::class)
    fun registerListener(listener: Any)

    /**
     * Unregister listener for modification.
     *
     * @param listener Listener instance.
     */
    @Throws(RemoteException::class)
    fun unregisterListener(listener: Any)

    companion object

}