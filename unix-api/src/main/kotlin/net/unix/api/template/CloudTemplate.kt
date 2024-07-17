package net.unix.api.template

import java.io.File
import net.unix.api.service.CloudService

/**
 * [CloudTemplate] allow control creation instance of [CloudService]'s
 */
interface CloudTemplate {
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