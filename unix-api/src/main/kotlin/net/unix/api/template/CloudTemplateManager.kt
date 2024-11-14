package net.unix.api.template

import net.unix.api.service.CloudService

/**
 * Manager for [CloudTemplate]'s.
 *
 * With this, you can control the [CloudTemplate]'s for [CloudService]'s.
 */
interface CloudTemplateManager {

    /**
     * All [CloudTemplate]'s.
     */
    val templates: Set<CloudTemplate>

    /**
     * Register template in [templates].
     *
     * @param template Template to register.
     */
    fun register(template: CloudTemplate)

    /**
     * Get [CloudTemplate] by name.
     *
     * @param name Template name.
     *
     * @return [CloudTemplate] instance or null, if not founded.
     */
    operator fun get(name: String): CloudTemplate?

    companion object
}