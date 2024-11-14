package net.unix.api.group

import java.io.File

/**
 * Represent savable [CloudGroup].
 *
 * @see CloudGroup
 */
interface SavableCloudGroup : CloudGroup {

    /**
     * Folder of group.
     */
    val folder: File

    /**
     * Save group properties to file.
     *
     * @param file Where to keep the properties.
     */
    fun save(file: File)

}