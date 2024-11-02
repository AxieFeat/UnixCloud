package net.unix.api.modification

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import java.io.File

interface ModificationManager {

    /**
     * Folder from which modifications will be loaded.
     */
    var folder: File

    /**
     * Get some [Modification] by name.
     *
     * @param name Modification name.
     *
     * @return Instance of [Modification] or null, if not founded.
     */
    operator fun get(name: String): Modification?

    /**
     * Load all modifications from [folder].
     *
     * @param silent If true - all errors will be ignored, else it can throw exceptions.
     *
     * @return List of all loaded modifications.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If modification with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    fun loadAll(silent: Boolean = true): List<Modification>

    /**
     * Load some [Modification] from file.
     *
     * @param file Modification file.
     *
     * @return Loaded modification.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If modification with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    fun load(file: File): Modification?

    companion object

}