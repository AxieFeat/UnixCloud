package net.unix.api.modification.module

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import java.io.File

/**
 * Class loader for [Module].
 */
interface ModuleClassLoader {

    /**
     * File to load.
     */
    val file: File

    /**
     * Info about current module.
     * If file is not module or corrupted - it can be null.
     */
    val info: ModuleInfo?

    /**
     * Is [Module] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded module. Null if not loaded.
     */
    val module: Module?

    /**
     * Load [Module]. It not calls [Module.onLoad] function.
     *
     * @return Loaded instance of [Module] or null, if throw exception.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If module with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    fun load(): Module?

    /**
     * Unload [Module]. It calls [Module.onUnload] function.
     *
     * @return True if success, else false.
     */
    fun unload(): Boolean

    /**
     * Reload [Module]. It calls [Module.onReload] function.
     *
     * @return True if success, else false.
     */
    fun reload(): Boolean

}