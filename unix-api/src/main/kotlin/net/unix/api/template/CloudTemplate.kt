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
     * Template name
     */
    var name: String

    /**
     * Template location
     */
    val templateFolder: File

    /**
     * Files, that will be copied on creation [CloudService]
     */
    var files: MutableList<CloudFile>

    companion object
}