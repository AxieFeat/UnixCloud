package net.unix.cloud.modification.module

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.cloud.CloudInstance
import java.io.File

object CloudModuleManager : ModuleManager {

    private val cachedModules = mutableMapOf<String, Module>()

    override var folder: File = CloudInstance.instance.locationSpace.module

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

            cachedModules[loader.info!!.name] = module

            module
        }
    }

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        val result = loader.load() ?: throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")

        result.onLoad()

        return result
    }

    override fun unload(module: Module): Boolean = module.loader.unload()
    override fun reload(module: Module): Boolean = module.loader.reload()

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
