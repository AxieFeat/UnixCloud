package net.unix.cloud.modification.module

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import java.io.File

object CloudModuleManager : ModuleManager {

    private val cachedModules = mutableMapOf<String, Module>()

    override var folder: File
        get() = TODO("Not yet implemented")
        set(value) {}

    override val modules: List<Module>
        get() = cachedModules.values.toList()

    override fun get(name: String): Module? = cachedModules[name]

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        if (loader.loaded) return loader.module!!

        throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")
    }

    override fun unload(module: Module): Boolean = module.loader.unload()
    override fun reload(module: Module): Boolean = module.loader.reload()
}