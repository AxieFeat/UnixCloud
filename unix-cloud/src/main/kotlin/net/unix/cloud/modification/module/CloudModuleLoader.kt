package net.unix.cloud.modification.module

import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleClassLoader
import java.io.File

class CloudModuleLoader(
    override val file: File
) : ModuleClassLoader {

    override val loaded: Boolean
        get() = TODO("Not yet implemented")

    override val module: Module?
        get() = TODO("Not yet implemented")

    override fun load(): Boolean {
        TODO("Not yet implemented")
    }

    override fun unload(): Boolean {
        TODO("Not yet implemented")
    }

    override fun reload(): Boolean {
        TODO("Not yet implemented")
    }
}