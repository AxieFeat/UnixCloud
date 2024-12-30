package net.unix.api.template

import net.unix.api.LocationSpace
import java.io.File
import java.rmi.RemoteException

/**
 * Represents manager for [SaveableTemplate]'s.
 *
 * @see [SaveableTemplate]
 */
interface SaveableTemplateManager : TemplateManager {

    /**
     * Load all templates from [LocationSpace.template] file.
     */
    @Throws(RemoteException::class)
    fun loadAllTemplates()

    /**
     * Load template from file. It will be registered in [templates]
     *
     * @param file File to be loaded from.
     */
    @Throws(RemoteException::class)
    fun loadTemplate(file: File): SaveableTemplate
}