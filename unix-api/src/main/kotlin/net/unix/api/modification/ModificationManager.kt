package net.unix.api.modification

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.remote.RemoteAccessible
import java.io.File
import java.rmi.RemoteException

interface ModificationManager : RemoteAccessible {

    /**
     * Folder from which modifications will be loaded.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var folder: File

    /**
     * Get some [Modification] by name.
     *
     * @param name Modification name.
     *
     * @return Instance of [Modification] or null, if not founded.
     */
    @Throws(RemoteException::class)
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
    @Throws(ModificationLoadException::class, ModificationExistException::class, RemoteException::class)
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
    @Throws(ModificationLoadException::class, ModificationExistException::class, RemoteException::class)
    fun load(file: File): Modification?

    companion object

}