package net.unix.api.modification.module

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
     * Is [Module] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded module. Null if not loaded.
     */
    val module: Module?

    /**
     * Load [Module].
     *
     * @return True if success, else false.
     */
    fun load(): Boolean

    /**
     * Unload [Module].
     *
     * @return True if success, else false.
     */
    fun unload(): Boolean

    /**
     * Reload [Module].
     *
     * @return True if success, else false.
     */
    fun reload(): Boolean

}