package net.unix.api.group

import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.pattern.Deletable
import net.unix.api.pattern.Saveable
import java.io.File

/**
 * Represent savable [CloudGroup].
 *
 * @see CloudGroup
 */
interface SaveableCloudGroup : CloudGroup, Deletable, Saveable {

    /**
     * Folder of group.
     */
    val folder: File

    /**
     * Save group properties to file.
     *
     * @param file Where to keep the properties.
     */
    override fun save(file: File)

    /**
     * Delete current group. It will delete all services of this group.
     *
     * @throws CloudGroupLimitException If group has active services.
     */
    @Throws(CloudGroupLimitException::class)
    override fun delete()

}