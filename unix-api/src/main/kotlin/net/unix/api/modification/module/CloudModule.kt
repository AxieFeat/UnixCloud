package net.unix.api.modification.module

import net.unix.api.event.EventManager
import net.unix.api.modification.Modification
import java.io.File

/**
 * Create an inheritor of this class to create a module.
 *
 * Also don't forget that to create a module, you must create a module.json file
 * or use the [ModuleInfo] annotation for inheritor class to create a module.
 */
abstract class CloudModule : Modification {

    private val listeners = mutableListOf<Any>()

    private val internalFolder: File? = null
    private val internalExecutable: File? = null
    private val internalInfo: ModuleInfo? = null

    /**
     * Module folder.
     */
    override val folder: File
        get() = internalFolder!!

    /**
     * Module executable file location.
     */
    override val executable: File
        get() = internalExecutable!!

    /**
     * Module info.
     *
     * name, version, authors etc.
     */
    override val info: ModuleInfo
        get() = internalInfo!!

    /**
     * Call when module loaded.
     */
    override fun onLoad() {}

    /**
     * Call when module reload.
     */
    open fun onReload() {}

    /**
     * Call when module unloaded.
     */
    open fun onUnload() {}

    /**
     * Register listener for module.
     *
     * @param listener Listener instance.
     */
    override fun registerListener(listener: Any) {
        EventManager.registerListeners(listener)
        listeners.add(listener)
    }

    /**
     * Unregister listener for module.
     *
     * @param listener Listener instance.
     */
    override fun unregisterListener(listener: Any) {
        EventManager.unregisterListeners(listener)
        listeners.remove(listener)
    }

    companion object
}