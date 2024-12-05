package net.unix.node.modification.module

import net.unix.api.LocationSpace
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.node.CloudExtension.or
import net.unix.node.CloudExtension.rem
import net.unix.node.event.callEvent
import net.unix.node.event.modification.module.ModuleLoadEvent
import net.unix.node.event.modification.module.ModuleReloadEvent
import net.unix.node.event.modification.module.ModuleUnloadEvent
import net.unix.node.logging.CloudLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

object CloudModuleManager : ModuleManager, KoinComponent {

    private fun readResolve(): Any = CloudModuleManager

    private val locationSpace: LocationSpace by inject()

    private val cachedModules = mutableMapOf<String, Module>()

    override var folder: File = locationSpace.module

    override val modules: List<Module>
        get() = cachedModules.values.toList()

    override fun get(name: String): Module? = cachedModules[name]

    override fun loadAll(silent: Boolean): List<Module> {
        val loaders = folder.listFiles()
            ?.filter { it.name.endsWith(".jar") }
            ?.map { CloudModuleLoader(it) }
            ?.filter { it.info != null }
            ?: run {
                if (!silent) throw IllegalArgumentException("File \"${folder.path}\" is not a folder!")
                else listOf()
            }

        val sortedLoaders = sortModulesByDependencies(loaders)

        return sortedLoaders.map { loader ->

            val module = loader.load() ?:
            throw ModificationLoadException("Could not load ${loader.info!!.name} extension, corrupted file?")

            CloudLogger.info("Loaded module ${module.info.name} v${module.info.version}")

            cachedModules[loader.info!!.name] = module

            module
        }
    }

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        val result = loader.load() ?: throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")

        val event = ModuleLoadEvent(result).callEvent()

        cachedModules[result.info.name] = result

        if (!event.cancelled) result.onLoad()

        return result
    }

    override fun unload(module: Module): Boolean {
        val event = ModuleUnloadEvent(module).callEvent()

        val unload = !event.cancelled % module.loader.unload() or false

        if(unload) cachedModules.remove(module.info.name)

        return unload
    }

    override fun reload(module: Module): Boolean {
        val event = ModuleReloadEvent(module).callEvent()

        val reload = !event.cancelled % module.loader.reload() or false

        return reload
    }

    private fun sortModulesByDependencies(modules: List<CloudModuleLoader>): List<CloudModuleLoader> {
        val moduleMap = modules.associateBy { it.info!!.name }
        val visited = mutableSetOf<String>()
        val sortedModules = mutableListOf<CloudModuleLoader>()

        modules.filter { it.info!!.depends.isEmpty() && it.info.soft.isEmpty() }.forEach { module ->
            if (module.info!!.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        modules.filter { it.info!!.depends.isNotEmpty() }.forEach { module ->
            if (module.info!!.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        modules.filter { it.info!!.soft.isNotEmpty() }.forEach { module ->
            if (module.info!!.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        return sortedModules
    }

    private fun process(
        loader: CloudModuleLoader,
        moduleMap: Map<String, CloudModuleLoader>,
        visited: MutableSet<String>,
        sortedModules: MutableList<CloudModuleLoader>
    ) {
        if (loader.info!!.name !in visited) {
            visited.add(loader.info.name)

            loader.info.depends.forEach { dependency ->
                moduleMap[dependency]?.let {
                    if (it.info!!.name !in visited) {
                        process(it, moduleMap, visited, sortedModules)
                    }
                }
            }

            sortedModules.add(loader)
        }
    }
}
