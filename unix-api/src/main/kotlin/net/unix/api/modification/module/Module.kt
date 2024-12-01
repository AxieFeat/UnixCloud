package net.unix.api.modification.module

import net.unix.api.modification.Modification
import java.io.File
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * Create an inheritor of this class to create a module.
 *
 * Also don't forget that to create a module, you must create a module.json file.
 */
interface Module : Modification {

    /**
     * Module loader.
     */
    @get:Throws(RemoteException::class)
    var loader: ModuleClassLoader

    /**
     * Module folder.
     */
    @get:Throws(RemoteException::class)
    override var folder: File

    /**
     * Module executable file location.
     */
    @get:Throws(RemoteException::class)
    override var executable: File

    /**
     * Module info.
     *
     * name, version, authors etc.
     */
    @get:Throws(RemoteException::class)
    var info: ModuleInfo

    /**
     * Call when module loaded.
     */
    @Throws(RemoteException::class)
    override fun onLoad() {}

    /**
     * Call when module reload.
     */
    @Throws(RemoteException::class)
    fun onReload() {}

    /**
     * Call when module unloaded.
     */
    @Throws(RemoteException::class)
    fun onUnload() {}

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