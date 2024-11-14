package net.unix.api.template

import java.io.File

/**
 * Represents savable [CloudTemplate]
 *
 * @see [CloudTemplate]
 */
interface SavableCloudTemplate : CloudTemplate {

    /**
     * Folder of template.
     */
    val folder: File

    /**
     * Save template properties to file.
     *
     * @param file Where to keep the properties.
     */
    fun save(file: File)

}