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
     * Create new instance of [CloudTemplate]. It will be registered via [register].
     *
     * @param name Name of template.
     * @param files Files of template,
     *
     * @return New instance of [CloudTemplate].
     */
    fun newInstance(
        name: String,
        files: MutableList<CloudFile>
    ): CloudTemplate

    /**
     * Delete template. It will be unregistered via [unregister].
     *
     * @param template Template to delete.
     */
    fun delete(template: CloudTemplate)

    /**
     * Register template in [templates].
     *
     * @param template Template to register.
     */
    fun register(template: CloudTemplate)

    /**
     * Unregister template from [templates].
     *
     * @param template Template to unregister.
     */
    fun unregister(template: CloudTemplate)

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