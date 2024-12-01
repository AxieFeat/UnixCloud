package net.unix.api.modification

import net.unix.api.pattern.Serializable
import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * Info about some [Modification].
 */
interface ModificationInfo : Serializable, Nameable, RemoteAccessible {

    /**
     * Path to [Modification] main class.
     */
    @get:Throws(RemoteException::class)
    val main: String

    /**
     * Modification name.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Modification version.
     */
    @get:Throws(RemoteException::class)
    val version: String

    /**
     * Modification description.
     */
    @get:Throws(RemoteException::class)
    val description: String

    /**
     * Modification website.
     */
    @get:Throws(RemoteException::class)
    val website: String

    /**
     * Modification authors.
     */
    @get:Throws(RemoteException::class)
    val authors: List<String>

    companion object

}