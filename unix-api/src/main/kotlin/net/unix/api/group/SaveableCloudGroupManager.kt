package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.LocationSpace
import java.io.File
import java.rmi.RemoteException

/**
 * Represents manager for [SaveableCloudGroup].
 *
 * @see CloudGroupManager
 */
interface SaveableCloudGroupManager : CloudGroupManager {

    /**
     * Load all groups from [LocationSpace.group] file.
     */
    @Throws(RemoteException::class)
    fun loadAllGroups()

    /**
     * Load group from file.
     *
     * It not add it to [CloudGroupManager.groups]
     *
     * @param file File to be loaded from.
     */
    @Throws(RemoteException::class)
    fun loadGroup(file: File): SaveableCloudGroup

    /**
     * Delete specific group.
     *
     * @param group Group to delete.
     *
     * @throws CloudGroupLimitException If group has active services.
     */
    @Throws(CloudGroupLimitException::class, RemoteException::class)
    fun delete(group: SaveableCloudGroup)

}