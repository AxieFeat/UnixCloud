package net.unix.api.modification.module

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.remote.RemoteAccessible
import java.io.File
import java.rmi.RemoteException

/**
 * Class loader for [Module].
 */
interface ModuleClassLoader : RemoteAccessible {

    /**
     * File to load.
     */
    @get:Throws(RemoteException::class)
    val file: File

    /**
     * Info about current module.
     * If file is not module or corrupted - it can be null.
     */
    @get:Throws(RemoteException::class)
    val info: ModuleInfo?

    /**
     * Is [Module] loaded.
     */
    @get:Throws(RemoteException::class)
    val loaded: Boolean

    /**
     * Loaded module. Null if not loaded.
     */
    @get:Throws(RemoteException::class)
    val module: Module?

    /**
     * Load [Module]. It not calls [Module.onLoad] function.
     *
     * @return Loaded instance of [Module] or null, if throw exception.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If module with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class, RemoteException::class)
    fun load(): Module?

    /**
     * Unload [Module]. It calls [Module.onUnload] function.
     *
     * @return True if success, else false.
     */
    @Throws(RemoteException::class)
    fun unload(): Boolean

    /**
     * Reload [Module]. It calls [Module.onReload] function.
     *
     * @return True if success, else false.
     */
    @Throws(RemoteException::class)
    fun reload(): Boolean

}