package net.unix.api.modification.extension

import net.unix.api.event.EventManager
import net.unix.api.modification.Modification
import java.io.File

/**
 * Create an inheritor of this class to create extension.
 *
 * Extensions are loaded BEFORE UnixCloud is initialized, keep this in mind!
 *
 * Also don't forget that to create extension, you must create extension.json file
 * or use the @ExtensionInfo annotation for inheritor class to create a module
 */
abstract class CloudExtension : Modification {

    private val listeners = mutableListOf<Any>()

    private val internalFolder: File? = null
    private val internalExecutable: File? = null
    private val internalInfo: ExtensionInfo? = null

    /**
     * Extension folder
     */
    override val folder: File
        get() = internalFolder!!

    /**
     * Extension executable file location
     */
    override val executable: File
        get() = internalExecutable!!

    /**
     * Extension info
     *
     * name, version, authors and etc
     */
    override val info: ExtensionInfo
        get() = internalInfo!!

    /**
     * Call when extension loaded
     */
    override fun onLoad() {}

    /**
     * Register listener for module
     *
     * @param listener Listener instance
     */
    override fun registerListener(listener: Any) {
        EventManager.registerListeners(listener)
        listeners.add(listener)
    }

    /**
     * Unregister listener for module
     *
     * @param listener Listener instance
     */
    override fun unregisterListener(listener: Any) {
        EventManager.unregisterListeners(listener)
        listeners.remove(listener)
    }

    companion object
}