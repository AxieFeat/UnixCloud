package net.unix.api.group

import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate

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
     * Get [CloudGroup] by name.
     *
     * @param name Group name.
     *
     * @return [CloudGroup] instance or null, if not founded.
     */
    operator fun get(name: String): CloudGroup?
}