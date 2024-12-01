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
abstract class CloudModule : Module {

    val listeners = mutableListOf<Any>()

    @Transient
    final override lateinit var loader: ModuleClassLoader
    final override lateinit var folder: File
    final override lateinit var executable: File
    final override lateinit var info: ModuleInfo

    final override var modification: ModificationInfo
        get() = info
        set(value) { info = value as ModuleInfo }

    override fun registerListener(listener: Any) {
        CloudEventManager.registerListeners(listener)
        listeners.add(listener)
    }

    override fun unregisterListener(listener: Any) {
        CloudEventManager.unregisterListeners(listener)
        listeners.remove(listener)
    }
}