package net.unix.api.template

import net.unix.api.pattern.Deletable
import net.unix.api.pattern.Serializable
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.CloudService
import java.rmi.RemoteException

/**
 * [CloudTemplate] allow control creation instance of [CloudService]'s.
 *
 * These templates allow you to customize the files that will be
 * used by the CloudService's that are launched.
 * Use [CloudFile] to specify which files should be used.
 */
interface CloudTemplate : PersistentDataHolder, Serializable, Nameable, Deletable, RemoteAccessible {

    /**
     * Template name.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Files, that will be copied on creation [CloudService].
     */
    @get:Throws(RemoteException::class)
    val files: MutableList<CloudFile>

    /**
     * Files, that will be copied back, after [CloudService] delete.
     */
    @get:Throws(RemoteException::class)
    val backFiles: MutableList<CloudFile>

    /**
     * Delete template.
     */
    @Throws(RemoteException::class)
    override fun delete()

    companion object
}