package net.unix.api.modification.module

import java.io.File

/**
 * Class loader for [CloudModule].
 */
interface ModuleClassLoader {

    /**
     * File to load.
     */
    val file: File

    /**
     * Is [CloudModule] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded module. Null if not loaded.
     */
    val module: CloudModule?

    /**
     * Load [CloudModule].
     *
     * @return True if success, else false.
     */
    fun load(): Boolean

    /**
     * Unload [CloudModule].
     *
     * @return True if success, else false.
     */
    fun unload(): Boolean

    /**
     * Reload [CloudModule].
     *
     * @return True if success, else false.
     */
    fun reload(): Boolean
}