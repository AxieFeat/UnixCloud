package net.unix.api.modification.extension

import net.unix.api.modification.ModificationManager
import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import java.io.File
import kotlin.jvm.Throws

/**
 * Manager for [CloudExtension]'s
 */
interface CloudExtensionManager : ModificationManager {
    /**
     * List of all [CloudExtension]'s
     */
    val modules: List<CloudExtension>

    /**
     * Get [CloudExtension] by name
     *
     * @param name Module name
     *
     * @return Instance of [CloudExtension] or null, if not founded
     */
    override fun get(name: String): CloudExtension?

    /**
     * Load [CloudExtension] from file
     *
     * @param file Module file
     *
     * @return Loaded module
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If extension with this name already loaded
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun load(file: File): CloudExtension

    companion object
}