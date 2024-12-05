package net.unix.api.node

import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents a node.
 *
 * Node it some instance of UnixCloud.
 */
interface Node : Nameable, RemoteAccessible {

    /**
     * Name of Node.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Time of start Node. Its unix time.
     */
    @get:Throws(RemoteException::class)
    val startTime: Long

    /**
     * Uptime of Node in milliseconds.
     */
    @get:Throws(RemoteException::class)
    val uptime: Long

    /**
     * Memory usage of Node in bytes.
     */
    @get:Throws(RemoteException::class)
    val usageMemory: Long

    /**
     * Free memory of Node in bytes.
     */
    @get:Throws(RemoteException::class)
    val freeMemory: Long

    /**
     * Max memory of Node in bytes.
     */
    @get:Throws(RemoteException::class)
    val maxMemory: Long
}