package net.unix.api.template

import java.io.File

interface CloudTemplateManager {

    /**
     * All [CloudTemplate]'s
     */
    val templates: Set<CloudTemplate>

    /**
     * Get [CloudTemplate] by name
     *
     * @param name Template name
     *
     * @return [CloudTemplate] instance or null, if not founded
     */
    operator fun get(name: String): CloudTemplate?

    /**
     * Create instance of [CloudTemplate]
     *
     * @param folder Template folder
     * @param file Files to copy
     *
     * @return Instance of [CloudTemplate]
     */
    fun createTemplate(folder: File, vararg file: CloudFile): CloudTemplate

    companion object
}