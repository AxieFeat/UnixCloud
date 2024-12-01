package net.unix.cloud.modification.extension

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.cloud.CloudLocationSpace
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.modification.extension.ExtensionLoadEvent
import java.io.File

object CloudExtensionManager : ExtensionManager {

    private fun readResolve(): Any = CloudExtensionManager

    override var folder: File = CloudLocationSpace.extension
    private val cachedExtensions = mutableMapOf<String, Extension>()

    override val extensions: List<Extension>
        get() = cachedExtensions.values.toList()

    override fun get(name: String): Extension? = cachedExtensions[name]

    override fun loadAll(silent: Boolean): List<Extension> {
        return (folder.listFiles()
            ?.filter { it.name.endsWith(".jar") }
            ?.map { CloudExtensionLoader(it) }
            ?.filter { it.info != null }
            ?: run {
                if (!silent) throw IllegalArgumentException("File \"${folder.path}\" is not a folder!")
                else listOf()
            }).map { loader ->
                val extension = loader.load() ?:
                throw ModificationLoadException("Could not load ${loader.info!!.name} extension, corrupted file?")

                val event = ExtensionLoadEvent(extension).callEvent()

                if (!event.cancelled) extension.onLoad()

                cachedExtensions[extension.info.name] = extension

                extension
            }
    }

    override fun load(file: File): Extension {
        val loader = CloudExtensionLoader(file)

        val result = loader.load() ?:
        throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")

        val event = ExtensionLoadEvent(result).callEvent()

        if (!event.cancelled) {
            cachedExtensions[result.info.name] = result
            result.onLoad()
        }

        return result
    }
}