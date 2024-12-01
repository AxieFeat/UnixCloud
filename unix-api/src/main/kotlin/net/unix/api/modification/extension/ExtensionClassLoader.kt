package net.unix.api.modification.extension

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.remote.RemoteAccessible
import java.io.File
import java.rmi.RemoteException

/**
 * Class loader for [Extension].
 */
interface ExtensionClassLoader : RemoteAccessible {

    /**
     * File to load.
     */
    @get:Throws(RemoteException::class)
    val file: File

    /**
     * Info about current extension.
     * If file is not extension or corrupted - it can be null.
     */
    @get:Throws(RemoteException::class)
    val info: ExtensionInfo?

    /**
     * Is [Extension] loaded.
     */
    @get:Throws(RemoteException::class)
    val loaded: Boolean

    /**
     * Loaded extension. Null if not loaded.
     */
    @get:Throws(RemoteException::class)
    val extension: Extension?

    /**
     * Load [Extension]. It not calls [Extension.onLoad] function.
     *
     * @return Loaded instance of [Extension] or null, if throw exception.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If extension with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class, RemoteException::class)
    fun load(): Extension?

}