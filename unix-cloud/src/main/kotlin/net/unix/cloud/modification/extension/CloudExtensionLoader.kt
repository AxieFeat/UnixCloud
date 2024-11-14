package net.unix.cloud.modification.extension

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionClassLoader
import net.unix.api.modification.extension.ExtensionInfo
import net.unix.cloud.CloudExtension.deserializeJson
import net.unix.cloud.CloudInstance
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.modification.extension.ExtensionLoadEvent
import net.unix.cloud.modification.module.CloudModuleManager
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CloudExtensionLoader(
    override val file: File
) : ExtensionClassLoader, URLClassLoader(arrayOf(file.toURI().toURL())) {

    private val entries = mutableListOf<JarEntry>().also { list ->
        JarFile(file).also {
            it.stream().forEach { e: JarEntry -> list.add(e) }
            it.close()
        }
    }

    private var cachedLoad = false
    private var cachedExtension: CloudExtension? = null

    override val loaded: Boolean
        get() = cachedLoad

    override val extension: Extension?
        get() = cachedExtension

    override val info: ExtensionInfo? = run {
        val info = (entries.find {
            it.name == "extension.json"
        } ?: return@run null)
            .let { JarFile(file).deserializeJson<MutableMap<String, Any>>(it) }
            .let { CloudExtensionInfo.deserialize(it) }

        return@run info
    }

    private fun CloudExtension.init(info: ExtensionInfo) {
        this.loader = this@CloudExtensionLoader
        this.info = info
        this.folder = File(CloudModuleManager.folder, "")
        this.executable = file

        cachedExtension = this
    }

    override fun load(): Extension? {
        if(loaded) return extension

        val info = (info ?: throw ModificationLoadException("File \"extension.json\" not found in ${file.name} or it corrupted!"))
            .also { info ->
                val loaded = CloudInstance.instance.extensionManager.extensions.map { it.info.name }

                if(loaded.contains(info.name)) throw ModificationExistException("Extension with name \"${info.name}\" already loaded!")
            }

        entries.filter {
            it.name.endsWith(".class")
        }.map {
            it.name.substring(0, it.name.length - 6).replace('/', '.')
        }.also {
            if(!it.contains(info.main)) throw ModificationLoadException("Main class \"${info.main}\" in extension \"${info.name}\" not found!")
        }.map {
            loadClass(it)
        }.forEach { clazz ->
            if (clazz.name == info.main) {
                val instance = try {
                    clazz.newInstance() as CloudExtension
                } catch (ex: ClassCastException) {
                    throw ModificationLoadException("Main class in extension \"${info.name}\" is not extends CloudExtension!")
                }

                instance.init(info)

                return instance
            }
        }

        return null
    }


}