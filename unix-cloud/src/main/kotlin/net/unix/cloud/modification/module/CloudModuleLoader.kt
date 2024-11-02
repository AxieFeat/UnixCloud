package net.unix.cloud.modification.module

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleClassLoader
import net.unix.api.modification.module.ModuleInfo
import net.unix.cloud.CloudExtension.deserializeJson
import net.unix.cloud.CloudExtension.print
import net.unix.cloud.CloudInstance
import net.unix.cloud.event.CloudEventManager
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CloudModuleLoader(
    override val file: File
) : ModuleClassLoader, URLClassLoader(arrayOf(file.toURI().toURL())) {

    private val entries = mutableListOf<JarEntry>()

    private var cachedLoad = false
    private var cachedModule: Module? = null

    override val loaded: Boolean
        get() = cachedLoad

    override val module: Module?
        get() = cachedModule


    private fun Module.init(info: CloudModuleInfo) {
        this.loader = this@CloudModuleLoader
        this.info = info
        this.folder = File(CloudModuleManager.folder, "")
        this.executable = file

        cachedModule = this
    }

    private fun CloudModule.disable() {
        this.listeners.forEach { CloudEventManager.unregisterListeners(it) }
        this.listeners.clear()
    }

    init {
        JarFile(file).also {
            it.stream().forEach { e: JarEntry -> entries.add(e) }
            it.close()
        }
    }

    override fun load(): Boolean {
        if(loaded) return false

        val info = (entries.find {
            it.name == "module.json"
        } ?: throw ModificationLoadException("File \"module.json\" not found in ${file.name}!"))
            .let { JarFile(file).deserializeJson<MutableMap<String, Any>>(it) }
            .let { CloudModuleInfo.deserialize(it) }
            .also { info ->
                val loaded = CloudInstance.instance.moduleManager.modules.map { it.info.name }

                if(loaded.contains(info.name)) throw ModificationExistException("Module with name \"${info.name}\" already loaded!")

                info.depends.forEach {
                    if(!loaded.contains(it)) {
                        throw ModificationLoadException(
                            "Cannot load \"${info.name}\" module, it depends from \"$it\"!"
                        )
                    }
                }
            }

        entries.filter {
            it.name.endsWith(".class")
        }.map {
            it.name.substring(0, it.name.length - 6).replace('/', '.')
        }.also {
            if(!it.contains(info.main)) throw ModificationLoadException("Main class \"${info.main}\" in module \"${info.name}\" not found!")
        }.map {
            loadClass(it)
        }.forEach { clazz ->
            if (clazz.name == info.main) {
                val instance = try {
                    clazz.newInstance() as CloudModule
                } catch (ex: ClassCastException) {
                    throw ModificationLoadException("Main class in module \"${info.name}\" is not extends CloudModule!")
                }

                instance.init(info)

                instance.onLoad()
            }
        }

        return true
    }

    override fun unload(): Boolean {
        if (!loaded) return false

        return true
    }

    override fun reload(): Boolean {
        if (!loaded) return false

        return true
    }
}