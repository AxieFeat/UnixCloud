package net.unix.cloud.modification.module

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleClassLoader
import net.unix.api.modification.module.ModuleInfo
import net.unix.cloud.CloudExtension.deserializeJson
import net.unix.cloud.CloudInstance
import net.unix.cloud.event.CloudEventManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile

class CloudModuleLoader(
    override val file: File
) : ModuleClassLoader, URLClassLoader(arrayOf(file.toURI().toURL())), KoinComponent {

    private val cloudModuleManager: CloudModuleManager by inject()

    private val entries = mutableListOf<JarEntry>().also { list ->
        JarFile(file).also {
            it.stream().forEach { e: JarEntry -> list.add(e) }
            it.close()
        }
    }

    private var cachedLoad = false
    private var cachedModule: CloudModule? = null

    override val loaded: Boolean
        get() = cachedLoad

    override val module: CloudModule?
        get() = cachedModule

    override val info: ModuleInfo? = run {
        val info = (entries.find {
            it.name == "module.json"
        } ?: return@run null)
            .let { JarFile(file).deserializeJson<MutableMap<String, Any>>(it) }
            .let { CloudModuleInfo.deserialize(it) }

        return@run info
    }

    private fun CloudModule.init(info: ModuleInfo) {
        this.loader = this@CloudModuleLoader
        this.info = info
        this.folder = File(CloudModuleManager.folder, "")
        this.executable = file

        cachedModule = this
    }

    private fun CloudModule.disableListeners(delete: Boolean = false) {
        this.listeners.forEach { CloudEventManager.unregisterListeners(it) }
        if (delete) this.listeners.clear()
    }

    private fun CloudModule.enableListeners() {
        this.listeners.forEach { CloudEventManager.registerListeners(it) }
    }

    override fun load(): Module? {
        if(loaded) return module

        val info = (info ?: throw ModificationLoadException("File \"module.json\" not found in ${file.name} or it corrupted!"))
            .also { info ->
                val loaded = cloudModuleManager.modules.map { it.info.name }

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

                return instance
            }
        }

        return null
    }

    override fun unload(): Boolean {
        if (!loaded) return false

        module?.disableListeners(true)
        module?.onUnload()

        return true
    }

    override fun reload(): Boolean {
        if (!loaded) return false

        module?.onReload()
        module?.disableListeners(false)
        module?.enableListeners()

        return true
    }
}