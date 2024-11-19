package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
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

    /**
     * Delete current group. It will delete all services of this group.
     *
     * @throws CloudGroupLimitException If group has active services.
     */
    @Throws(CloudGroupLimitException::class)
    fun delete()

}