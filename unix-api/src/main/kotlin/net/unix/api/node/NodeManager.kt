package net.unix.api.node

import net.unix.api.network.client.Client
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This interface represents manager for [Node]'s.
 */
interface NodeManager : RemoteAccessible {

    /**
     * Client of node bridge.
     */
    val client: Client

    /**
     * Set of all [Node]'s
     */
    @get:Throws(RemoteException::class)
    val nodes: Set<Node>

    /**
     * Configure client for messaging between nodes.
     */
    @Throws(RemoteException::class)
    fun configure(client: Client)

    /**
     * Get [Node] by [name].
     *
     * @param name Name of node.
     *
     * @return Instance of [Node] or null, if not founded.
     */
    @Throws(RemoteException::class)
    operator fun get(name: String): Node?

}