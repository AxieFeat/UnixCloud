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

        val sortedLoaders = sortModulesByDependencies(loaders.map { it.info as CloudModuleInfo })
        return sortedLoaders.mapNotNull { info ->
            val module = loaders.find { it.info === info }!!.load()
            module?.let {
                cachedModules[it.info.name] = it
                it.onLoad()
            }
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

    fun sortModulesByDependencies(modules: List<CloudModuleInfo>): List<CloudModuleInfo> {
        val moduleMap = modules.associateBy { it.name }
        val visited = mutableSetOf<String>()
        val sortedModules = mutableListOf<CloudModuleInfo>()

        modules.filter { it.depends.isEmpty() && it.soft.isEmpty() }.forEach { module ->
            if (module.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        modules.filter { it.depends.isNotEmpty() }.forEach { module ->
            if (module.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        modules.filter { it.soft.isNotEmpty() }.forEach { module ->
            if (module.name !in visited) {
                process(module, moduleMap, visited, sortedModules)
            }
        }

        return sortedModules
    }

    private fun process(
        module: CloudModuleInfo,
        moduleMap: Map<String, CloudModuleInfo>,
        visited: MutableSet<String>,
        sortedModules: MutableList<CloudModuleInfo>
    ) {
        if (module.name !in visited) {
            visited.add(module.name)

            module.depends.forEach { dependency ->
                moduleMap[dependency]?.let {
                    if (it.name !in visited) {
                        process(it, moduleMap, visited, sortedModules)
                    }
                }
            }

            sortedModules.add(module)
        }
    }
}
