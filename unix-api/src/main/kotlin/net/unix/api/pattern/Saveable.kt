package net.unix.api.pattern

import java.io.File

/**
 * This interface represents the object, that's can be saved to file.
 */
interface Saveable {

    /**
     * Save object to file.
     *
     * @param file To what file save.
     */
    fun save(file: File)

}