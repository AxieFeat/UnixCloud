package net.unix.node.modification.extension

import net.unix.api.modification.ModificationInfo
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionClassLoader
import net.unix.api.modification.extension.ExtensionInfo
import net.unix.node.event.CloudEventManager
import java.io.File

/**
 * Create an inheritor of this class to create extension.
 *
 * Extensions are loaded BEFORE UnixCloud is initialized, keep this in mind!
 * For example, you won't be able to use CloudLogger because it hasn't been initiated yet.
 *
 * Also don't forget that to create extension, you must create extension.json file.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class CloudExtension : Extension {

    val listeners = mutableListOf<Any>()

    @Transient
    override lateinit var loader: ExtensionClassLoader

    final override lateinit var folder: File
    final override lateinit var executable: File
    final override lateinit var info: ExtensionInfo

    final override var modification: ModificationInfo
        get() = info
        set(value) { info = value as ExtensionInfo }

    override fun registerListener(listener: Any) {
        CloudEventManager.registerListeners(listener)
        listeners.add(listener)
    }

    override fun unregisterListener(listener: Any) {
        CloudEventManager.unregisterListeners(listener)
        listeners.remove(listener)
    }
}