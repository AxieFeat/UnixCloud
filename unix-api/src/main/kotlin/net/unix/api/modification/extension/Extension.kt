package net.unix.api.modification.extension

import net.unix.api.modification.Modification
import java.io.File

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
    var loader: ExtensionClassLoader

    /**
     * Extension folder.
     */
    override var folder: File

    /**
     * Extension executable file location.
     */
    override var executable: File

    /**
     * Extension info.
     *
     * name, version, authors etc.
     */
    var info: ExtensionInfo

    /**
     * Call when extension loaded.
     */
    override fun onLoad() {}

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