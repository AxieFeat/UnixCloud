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

        val sortedLoaders = sortLoadersByCloudModuleInfo(loaders.map { it.info as CloudModuleInfo })

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

    private fun moduleWeight(info: CloudModuleInfo, moduleMap: Map<String, CloudModuleInfo>): Double {
    var weight = info.priority * 1000

    if (info.depends.isEmpty() && info.soft.isEmpty()) {
        weight += 10000
    }

    val hasDepends = info.depends.any { it in moduleMap }
    if (hasDepends) {
        weight += 5000
    }

    val dependsWeight = info.depends
        .filter { it in moduleMap }
        .sumOf { moduleMap[it]!!.priority }
    weight -= dependsWeight * 100

    val hasSoftDepends = info.soft.any { it in moduleMap }
    if (hasSoftDepends && !hasDepends) {
        weight += 2000
    }

    val softWeight = info.soft
        .filter { it in moduleMap }
        .sumOf { moduleMap[it]!!.priority }
    weight -= softWeight * 50

    return weight
    }

    fun sortLoadersByCloudModuleInfo(info: List<CloudModuleInfo>): List<CloudModuleInfo> {
        val moduleMap = info.associateBy { it.name }

        return info.sortedWith(compareBy({ -moduleWeight(it, moduleMap) }, { -it.priority }, { it.name }))
    }
}
