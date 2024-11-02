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
        // TODO переделать загрузку: эта функция должна сначала отсортировать все модули по зависимостям и приоритетам.
        folder.listFiles()?.forEach {
            load(it)
        }

        return listOf()
    }

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        if (loader.load()) return loader.module!!

        throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")
    }

    override fun unload(module: Module): Boolean = module.loader.unload()
    override fun reload(module: Module): Boolean = module.loader.reload()
}