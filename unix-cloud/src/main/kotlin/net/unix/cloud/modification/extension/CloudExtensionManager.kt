package net.unix.cloud.modification.extension

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import java.io.File

object CloudExtensionManager : ExtensionManager {

    private val cachedExtensions = mutableMapOf<String, Extension>()

    override val extensions: List<Extension>
        get() = cachedExtensions.values.toList()

    override fun get(name: String): Extension? = cachedExtensions[name]

    override fun load(file: File): Extension {
        val loader = CloudExtensionLoader(file)

        if (loader.load()) return loader.extension!!

        throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")
    }
}