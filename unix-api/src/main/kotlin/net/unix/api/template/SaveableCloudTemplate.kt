package net.unix.api.template

import net.unix.api.pattern.Saveable
import java.io.File

/**
 * Represents savable [CloudTemplate]
 *
 * @see [CloudTemplate]
 */
interface SaveableCloudTemplate : CloudTemplate, Saveable {

    /**
     * Folder of template.
     */
    val folder: File

    /**
     * Save template properties to file.
     *
     * @param file Where to keep the properties.
     */
    override fun save(file: File)

}