package net.unix.api.group

import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import java.util.UUID

/**
 * Manager for [CloudGroup]'s.
 */
interface CloudGroupManager {

    /**
     * All [CloudGroup]'s.
     */
    val groups: Set<CloudGroup>

    /**
     * Create instance of [CloudGroup].
     *
     * @param name Group name.
     * @param static Is group static.
     * @param host Group host.
     * @param availablePorts Ports, which [CloudService] can be started.
     * @param startupCount Count of [CloudService]'s that will start with UnixCloud.
     * @param templates [CloudTemplate]'s of group.
     *
     * @return Instance of [CloudGroup].
     */
    fun createGroup(
        name: String,
        static: Boolean,
        host: String,
        availablePorts: MutableList<Int>,
        startupCount: Int,
        templates: MutableList<CloudTemplate>
    ): CloudGroup

    /**
     * Is exist [CloudGroup]'s with one name.
     *
     * @param name Name for check.
     *
     * @return True, if count of [CloudGroup]'s with this name > 1, else false
     */
    fun duplicates(name: String): Boolean = get(name).count() > 1

    /**
     * Get [CloudGroup] by name.
     *
     * @param name Group name.
     *
     * @return List of [CloudGroup]'s with this name, can be empty.
     */
    operator fun get(name: String): List<CloudGroup>

    /**
     * Get [CloudGroup] by [UUID].
     *
     * @param uuid The uuid.
     *
     * @return Instance of [CloudGroup] or null, if not founded.
     */
    operator fun get(uuid: UUID): CloudGroup?

}