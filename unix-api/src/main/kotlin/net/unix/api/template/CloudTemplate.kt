package net.unix.api.template

import net.unix.api.Serializable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.service.CloudService

/**
 * [CloudTemplate] allow control creation instance of [CloudService]'s.
 *
 * These templates allow you to customize the files that will be
 * used by the CloudService's that are launched.
 * Use [CloudFile] to specify which files should be used.
 */
interface CloudTemplate : PersistentDataHolder, Serializable {

    /**
     * Template name.
     */
    var name: String

    /**
     * Files, that will be copied on creation [CloudService].
     */
    var files: MutableList<CloudFile>

    /**
     * Files, that will be copied back, after [CloudService] delete.
     */
    var backFiles: MutableList<CloudFile>

    /**
     * Delete template.
     */
    fun delete()

    companion object
}