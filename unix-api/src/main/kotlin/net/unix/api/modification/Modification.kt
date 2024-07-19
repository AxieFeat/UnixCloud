package net.unix.api.modification

import java.io.File

/**
 * General interface for UnixCloud modifications
 */
interface Modification {

    /**
     * Module folder location
     */
    val folder: File

    /**
     * Module executable file location
     */
    val executable: File

    /**
     * Info about current modification
     */
    val info: ModificationInfo

    /**
     * Call when modification loaded
     */
    fun onLoad()
}