package net.unix.api.modification.module

import net.unix.api.modification.Modification
import java.io.File

/**
 * Create an inheritor of this class to create a module.
 *
 * Also don't forget that to create a module, you must create a module.json file.
 */
interface Module : Modification {

    /**
     * Module loader.
     */
    var loader: ModuleClassLoader

    /**
     * Module folder.
     */
    override var folder: File

    /**
     * Module executable file location.
     */
    override var executable: File

    /**
     * Module info.
     *
     * name, version, authors etc.
     */
    var info: ModuleInfo

    /**
     * Call when module loaded.
     */
    override fun onLoad() {}

    /**
     * Call when module reload.
     */
    fun onReload() {}

    /**
     * Call when module unloaded.
     */
    fun onUnload() {}

    /**
     * Register listener for module.
     *
     * @param listener Listener instance.
     */
    override fun registerListener(listener: Any)

    /**
     * Unregister listener for module.
     *
     * @param listener Listener instance.
     */
    override fun unregisterListener(listener: Any)

    companion object

}