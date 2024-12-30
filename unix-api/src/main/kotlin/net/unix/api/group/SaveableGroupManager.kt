package net.unix.api.group

import net.unix.api.group.exception.GroupLimitException
import net.unix.api.LocationSpace
import java.io.File
import java.rmi.RemoteException

/**
 * Represents manager for [SaveableGroup].
 *
 * @see GroupManager
 */
interface SaveableGroupManager : GroupManager {

    /**
     * Load all groups from [LocationSpace.group] file.
     */
    @Throws(RemoteException::class)
    fun loadAllGroups()

    /**
     * Load group from file.
     *
     * It not add it to [GroupManager.groups]
     *
     * @param file File to be loaded from.
     */
    @Throws(RemoteException::class)
    fun loadGroup(file: File): SaveableGroup

    /**
     * Delete specific group.
     *
     * @param group Group to delete.
     *
     * @throws GroupLimitException If group has active services.
     */
    @Throws(GroupLimitException::class, RemoteException::class)
    fun delete(group: SaveableGroup)

}