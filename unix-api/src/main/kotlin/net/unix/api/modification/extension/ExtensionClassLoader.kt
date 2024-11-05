package net.unix.api.modification.extension

import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
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
     * Info about current extension.
     * If file is not extension or corrupted - it can be null.
     */
    val info: ExtensionInfo?

    /**
     * Is [Extension] loaded.
     */
    val loaded: Boolean

    /**
     * Loaded extension. Null if not loaded.
     */
    val extension: Extension?

    /**
     * Load [Extension]. It not calls [Extension.onLoad] function.
     *
     * @return Loaded instance of [Extension] or null, if throw exception.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If extension with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    fun load(): Extension?

}