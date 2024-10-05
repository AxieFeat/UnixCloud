package net.unix.api.modification.extension

import java.io.File

/**
 * Class loader for [Extension].
 */
interface ExtensionClassLoader {

    /**
     * File to load.
     */
    val file: File

    /**
     * Is [Extension] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded extension. Null if not loaded.
     */
    val extension: Extension?

    /**
     * Load [Extension].
     *
     * @return True if success, else false.
     */
    fun load(): Boolean

}