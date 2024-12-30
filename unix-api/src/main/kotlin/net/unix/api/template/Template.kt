package net.unix.api.template

import net.unix.api.pattern.Deletable
import net.unix.api.pattern.Serializable
import net.unix.api.pattern.Nameable
import net.unix.api.persistence.PersistentDataHolder
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.Service
import java.rmi.RemoteException

/**
 * [Template] allow control creation instance of [Service]'s.
 *
 * These templates allow you to customize the files that will be
 * used by the CloudService's that are launched.
 * Use [CloudFile] to specify which files should be used.
 */
interface Template : PersistentDataHolder, Serializable, Nameable, Deletable, RemoteAccessible {

    /**
     * Template name.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Files, that will be copied on creation [Service].
     */
    @get:Throws(RemoteException::class)
    val files: MutableList<CloudFile>

    /**
     * Files, that will be copied back, after [Service] delete.
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