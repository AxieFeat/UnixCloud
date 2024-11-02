package net.unix.cloud.modification.extension

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.Module
import java.io.File

object CloudExtensionManager : ExtensionManager {

    override var folder: File
        get() = TODO("Not yet implemented")
        set(value) {}

    private val cachedExtensions = mutableMapOf<String, Extension>()

    override val extensions: List<Extension>
        get() = cachedExtensions.values.toList()

    override fun get(name: String): Extension? = cachedExtensions[name]

    override fun loadAll(silent: Boolean): List<Module> {
        TODO("Not yet implemented")
    }

    override fun load(file: File): Extension {
        val loader = CloudExtensionLoader(file)

        if (loader.load()) return loader.extension!!

        throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")
    }
}