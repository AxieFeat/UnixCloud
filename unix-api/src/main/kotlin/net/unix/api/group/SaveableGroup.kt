package net.unix.api.group

import net.unix.api.group.exception.GroupLimitException
import net.unix.api.pattern.Deletable
import net.unix.api.pattern.Saveable
import java.io.File
import java.rmi.RemoteException

/**
 * Represent savable [Group].
 *
 * @see Group
 */
interface SaveableGroup : Group, Deletable, Saveable {

    /**
     * Save group properties to file.
     *
     * @param file Where to keep the properties.
     */
    @Throws(RemoteException::class)
    override fun save(file: File)

    /**
     * Delete current group. It will delete all services of this group.
     *
     * @throws GroupLimitException If group has active services.
     */
    @Throws(GroupLimitException::class, RemoteException::class)
    override fun delete()

}