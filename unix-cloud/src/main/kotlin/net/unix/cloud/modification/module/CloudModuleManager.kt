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

        val sortedLoaders = sortLoadersByCloudModuleInfo(loaders)

        return sortedLoaders.map { loader ->
            val module = loader.load()
            if (module != null) {
                cachedModules[module.info.name] = module
                module.onLoad()
            }
            module
        }.filterNotNull()
    }

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        val result = loader.load() ?: throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")

        result.onLoad()

        return result
    }

    override fun unload(module: Module): Boolean = module.loader.unload()
    override fun reload(module: Module): Boolean = module.loader.reload()
}

    private fun sortLoadersByCloudModuleInfo(loaders: List<CloudModuleLoader>): List<CloudModuleLoader> {
        val moduleMap = loaders.associateBy { it.info!!.name }

        fun moduleWeight(loader: CloudModuleLoader): Double {
            val info = loader.info!!
            var weight = info.priority * 1000

            val dependsWeight = info.depends
                .filter { it in moduleMap }
                .sumOf { moduleMap[it]!!.info!!.priority }
            weight -= dependsWeight * 10

            val softWeight = info.soft
                .filter { it in moduleMap }
                .sumOf { moduleMap[it]!!.info!!.priority }
            weight -= softWeight * 5

            return weight
        }

        return loaders.sortedWith(compareBy({ moduleWeight(it) }, { it.info!!.name }))
    }
