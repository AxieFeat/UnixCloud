package net.unix.cloud.modification.module

import net.unix.api.modification.ModificationInfo
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleClassLoader
import net.unix.api.modification.module.ModuleInfo
import net.unix.cloud.event.CloudEventManager
import java.io.File

/**
 * Create an inheritor of this class to create a module.
 *
 * Also don't forget that to create a module, you must create a module.json file.
 */
class CloudModule : Module {

    private val listeners = mutableListOf<Any>()

    override lateinit var loader: ModuleClassLoader
    override lateinit var folder: File
    override lateinit var executable: File
    override lateinit var info: ModuleInfo

    override lateinit var modification: ModificationInfo

    override fun registerListener(listener: Any) {
        CloudEventManager.registerListeners(listener)
        listeners.add(listener)
    }

    override fun unregisterListener(listener: Any) {
        CloudEventManager.unregisterListeners(listener)
        listeners.remove(listener)
    }
}