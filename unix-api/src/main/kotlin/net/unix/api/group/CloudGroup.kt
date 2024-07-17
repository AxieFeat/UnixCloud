package net.unix.api.group

import net.unix.api.template.CloudTemplate
import net.unix.api.service.CloudService

/**
 * Generic template for starting instances of [CloudService]
 */
interface CloudGroup {
    /**
     * Cloud group name
     */
    var name: String

    /**
     * Is group static
     *
     * Static [CloudService] of group don't delete after [CloudService] stop
     */
    val static: Boolean

    /**
     * Group host
     */
    val host: String

    /**
     * Ports, which [CloudService] can be started
     */
    val availablePorts: MutableList<Int>

    /**
     * Count of [CloudService]'s that will start with UnixCloud
     */
    val startupCount: Int

    /**
     * [CloudTemplate]'s of group
     */
    val templates: MutableList<CloudTemplate>

    companion object
}