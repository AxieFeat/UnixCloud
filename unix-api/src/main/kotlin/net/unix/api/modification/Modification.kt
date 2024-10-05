package net.unix.api.modification

import java.io.File

/**
 * General interface for UnixCloud modifications.
 */
interface Modification {

    /**
     * Modification folder location.
     */
    var folder: File

    /**
     * Modification executable file location.
     */
    var executable: File

    /**
     * Modification information
     */
    var modification: ModificationInfo

    /**
     * Call when modification loaded.
     */
    fun onLoad()

    /**
     * Register listener for modification.
     *
     * @param listener Listener instance.
     */
    fun registerListener(listener: Any)

    /**
     * Unregister listener for modification.
     *
     * @param listener Listener instance.
     */
    fun unregisterListener(listener: Any)

    companion object

}