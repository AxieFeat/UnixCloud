package net.unix.api.template

import net.unix.api.Serializable
import net.unix.api.persistence.PersistentDataHolder
import java.io.File
import net.unix.api.service.CloudService

/**
 * [CloudTemplate] allow control creation instance of [CloudService]'s
 */
interface CloudTemplate : PersistentDataHolder, Serializable {

    /**
     * Template location
     */
    var templateFolder: File

    /**
     * Files, that will be copied on creation [CloudService]
     */
    val files: MutableList<CloudFile>

    companion object

}