package net.unix.api.template

import net.unix.api.LocationSpace
import java.io.File

/**
 * Represents manager for [SavableCloudTemplate].
 *
 * @see SavableCloudTemplate
 */
interface SavableCloudTemplateManager : CloudTemplateManager {

    /**
     * Load all templates from [LocationSpace.template] file.
     */
    fun loadAllTemplates()

    /**
     * Load template from file. It will be registered in [templates]
     *
     * @param file File to be loaded from.
     */
    fun loadTemplate(file: File): SavableCloudTemplate
}