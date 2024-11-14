package net.unix.api.template

import java.io.File

/**
 * Represents manager for [SavableCloudTemplate].
 *
 * @see SavableCloudTemplate
 */
interface SavableCloudTemplateManager : CloudTemplateManager {

    /**
     * Load template from file.
     *
     * It not add it to [CloudTemplateManager.templates]
     *
     * @param file File to be loaded from.
     */
    fun loadTemplate(file: File): SavableCloudTemplate

}