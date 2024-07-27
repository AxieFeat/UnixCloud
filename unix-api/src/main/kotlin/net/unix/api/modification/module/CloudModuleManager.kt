package net.unix.api.modification.module

import net.unix.api.modification.ModificationManager
import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import java.io.File
import kotlin.jvm.Throws

/**
 * Manager for [CloudModule]'s
 */
interface CloudModuleManager : ModificationManager {
    /**
     * List of all [CloudModule]'s
     */
    val modules: List<CloudModule>

    /**
     * Get [CloudModule] by name
     *
     * @param name Module name
     *
     * @return Instance of [CloudModule] or null, if not founded
     */
    override fun get(name: String): CloudModule?

    /**
     * Load [CloudModule] from file
     *
     * @param file Module file
     *
     * @return Loaded module
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If module with this name already loaded
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun load(file: File): CloudModule

    /**
     * Unload specific [CloudModule]
     *
     * @param module Module to unload
     *
     * @return True if success, else false
     */
    fun unload(module: CloudModule): Boolean

    /**
     * Reload specific [CloudModule]
     *
     * @param module Module to reload
     *
     * @return True if success, else false
     */
    fun reload(module: CloudModule): Boolean

    companion object
}