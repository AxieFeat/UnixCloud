package net.unix.cloud.modification.extension

import net.unix.api.modification.extension.CloudExtension
import net.unix.api.modification.extension.ExtensionManager
import java.io.File

object CloudExtensionManager : ExtensionManager {

    private val cachedExtensions = mutableMapOf<String, CloudExtension>()

    override val modules: List<CloudExtension>
        get() = cachedExtensions.values.toList()

    override fun get(name: String): CloudExtension? = cachedExtensions[name]

    override fun load(file: File): CloudExtension {
        TODO("Not yet implemented")
    }
}