package net.unix.api.modification.extension

import net.unix.api.modification.Modification
import java.io.File
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * Create an inheritor of this class to create extension.
 *
 * Extensions are loaded BEFORE UnixCloud is initialized, keep this in mind!
 *
 * Also don't forget that to create extension, you must create extension.json file.
 */
interface Extension : Modification {

    /**
     * Extension loader.
     */
    @get:Throws(RemoteException::class)
    var loader: ExtensionClassLoader

    /**
     * Extension folder.
     */
    @get:Throws(RemoteException::class)
    override var folder: File

    /**
     * Extension executable file location.
     */
    @get:Throws(RemoteException::class)
    override var executable: File

    /**
     * Extension info.
     *
     * name, version, authors etc.
     */
    @get:Throws(RemoteException::class)
    var info: ExtensionInfo

    /**
     * Call when extension loaded.
     */
    @Throws(RemoteException::class)
    override fun onLoad() {}

    /**
     * Register listener for module.
     *
     * @param listener Listener instance.
     */
    @Throws(RemoteException::class)
    override fun registerListener(listener: Any)

    /**
     * Unregister listener for module.
     *
     * @param listener Listener instance.
     */
    @Throws(RemoteException::class)
    override fun unregisterListener(listener: Any)

    companion object

}