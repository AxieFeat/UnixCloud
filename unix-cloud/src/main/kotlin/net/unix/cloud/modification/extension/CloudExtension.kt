package net.unix.cloud.modification.extension

import net.unix.api.modification.ModificationInfo
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionClassLoader
import net.unix.api.modification.extension.ExtensionInfo
import net.unix.cloud.event.CloudEventManager
import java.io.File

/**
 * Create an inheritor of this class to create extension.
 *
 * Extensions are loaded BEFORE UnixCloud is initialized, keep this in mind!
 *
 * Also don't forget that to create extension, you must create extension.json file.
 */
class CloudExtension : Extension {

    private val listeners = mutableListOf<Any>()

    override lateinit var loader: ExtensionClassLoader

    override lateinit var folder: File
    override lateinit var executable: File
    override lateinit var info: ExtensionInfo

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