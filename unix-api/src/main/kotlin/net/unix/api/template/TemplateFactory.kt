package net.unix.api.template

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents a simple factory of [Template]'s.
 */
interface TemplateFactory : RemoteAccessible {

    /**
     * Create new instance of [Template].
     *
     * @param name Name of template.
     * @param persistent Persistent container of template.
     * @param files Files of template.
     * @param backFiles Back files of template.
     *
     * @return New instance of [Template].
     */
    @Throws(RemoteException::class)
    fun create(
        name: String,
        persistent: PersistentDataContainer,
        files: MutableList<CloudFile>,
        backFiles: MutableList<CloudFile>
    ): Template

}