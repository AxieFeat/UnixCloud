package net.unix.api.group

import java.io.File

/**
 * Represents manager for [SavableCloudGroup].
 *
 * @see CloudGroupManager
 */
interface SavableCloudGroupManager : CloudGroupManager {

    /**
     * Load group from file.
     *
     * It not add it to [CloudGroupManager.groups]
     *
     * @param file File to be loaded from.
     */
    fun loadGroup(file: File): SavableCloudGroup

}