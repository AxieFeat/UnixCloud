package net.unix.api.modification.extension

import java.io.File

/**
 * Class loader for [CloudExtension].
 */
interface ExtensionClassLoader {

    /**
     * File to load.
     */
    val file: File

    /**
     * Is [CloudExtension] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded extension. Null if not loaded.
     */
    val module: CloudExtension?

    /**
     * Load [CloudExtension].
     *
     * @return True if success, else false.
     */
    fun load(): Boolean
}