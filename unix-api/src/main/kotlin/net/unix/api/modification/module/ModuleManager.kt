package net.unix.api.modification.module

import net.unix.api.modification.Modification
import net.unix.api.modification.ModificationManager
import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import java.io.File

/**
 * Manager for [Module]'s.
 */
interface ModuleManager : ModificationManager {

    /**
     * List of all [Module]'s.
     */
    val modules: List<Module>

    /**
     * Get [Module] by name.
     *
     * @param name Module name.
     *
     * @return Instance of [Module] or null, if not founded.
     */
    override fun get(name: String): Module?

    /**
     * Load all modules from [folder].
     *
     * @param silent If true - all errors will be ignored, else it can throw exceptions.
     *
     * @return List of all loaded modules.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If module with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun loadAll(silent: Boolean): List<Module>

    /**
     * Load [Module] from file.
     *
     * @param file Module file.
     *
     * @return Loaded module.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If module with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun load(file: File): Module

    /**
     * Unload specific [Module].
     *
     * @param module Module to unload.
     *
     * @return True if success, else false.
     */
    fun unload(module: Module): Boolean

    /**
     * Reload specific [Module].
     *
     * @param module Module to reload.
     *
     * @return True if success, else false.
     */
    fun reload(module: Module): Boolean

    companion object

}