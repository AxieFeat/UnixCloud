package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.LocationSpace
import java.io.File

/**
 * Represents manager for [SaveableCloudGroup].
 *
 * @see CloudGroupManager
 */
interface SaveableCloudGroupManager : CloudGroupManager {

    /**
     * Load all groups from [LocationSpace.group] file.
     */
    fun loadAllGroups()

    /**
     * Load group from file.
     *
     * It not add it to [CloudGroupManager.groups]
     *
     * @param file File to be loaded from.
     */
    fun loadGroup(file: File): SaveableCloudGroup

    /**
     * Delete specific group.
     *
     * @param group Group to delete.
     *
     * @throws CloudGroupLimitException If group has active services.
     */
    @Throws(CloudGroupLimitException::class)
    fun delete(group: SaveableCloudGroup)

}