package net.unix.cloud.modification.module

import net.unix.api.modification.module.CloudModule
import net.unix.api.modification.module.ModuleManager
import java.io.File

object CloudModuleManager : ModuleManager {

    private val cachedModules = mutableMapOf<String, CloudModule>()

    override val modules: List<CloudModule>
        get() = cachedModules.values.toList()

    override fun get(name: String): CloudModule? = cachedModules[name]

    override fun load(file: File): CloudModule {
        TODO("Not yet implemented")
    }

    override fun unload(module: CloudModule): Boolean {
        TODO("Not yet implemented")
    }

    override fun reload(module: CloudModule): Boolean {
        TODO("Not yet implemented")
    }
}