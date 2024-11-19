package net.unix.api.group

import net.unix.api.template.CloudTemplate
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
     * Unregister group from [groups].
     *
     * @param group Group to unregister.
     */
    fun unregister(group: CloudGroup)

    /**
     * Create new instance of [CloudGroup]. It will be registered via [register].
     *
     * @param uuid UUID of group.
     * @param name Name of group.
     * @param serviceLimit Limit of service for group.
     * @param executableFile Path to executable file in prepared service.
     * @param templates List of templates.
     * @param type Type of group.
     *
     * @return New instance of [CloudGroup].
     */
    fun newInstance(uuid: UUID,
                    name: String,
                    serviceLimit: Int,
                    executableFile: String,
                    templates: MutableList<CloudTemplate> = mutableListOf(),
                    type: CloudGroupType? = null
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