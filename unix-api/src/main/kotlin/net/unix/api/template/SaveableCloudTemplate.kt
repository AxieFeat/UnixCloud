package net.unix.api.template

import net.unix.api.pattern.Saveable
import java.io.File
import java.rmi.RemoteException

/**
 * Represents savable [CloudTemplate]
 *
 * @see [CloudTemplate]
 */
interface SaveableCloudTemplate : CloudTemplate, Saveable {

    /**
     * Folder of template.
     */
    @get:Throws(RemoteException::class)
    val folder: File

    /**
     * Save template properties to file.
     *
     * @param file Where to keep the properties.
     */
    @Throws(RemoteException::class)
    override fun save(file: File)

}