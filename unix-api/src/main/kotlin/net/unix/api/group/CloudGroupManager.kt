package net.unix.api.group

import java.util.UUID

/**
 * Manager for [CloudGroup]'s.
 *
 * With this you can manage [CloudGroup]'s.
 */
interface CloudGroupManager {

    /**
     * All [CloudGroup]'s.
     */
    val groups: Set<CloudGroup>

    /**
     * Register group in [groups].
     *
     * @param group Group to register.
     */
    fun register(group: CloudGroup)

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