package net.unix.api.bridge

import net.unix.api.network.server.Server
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import java.util.*
import kotlin.jvm.Throws

/**
 * Represents bridge for messaging unix and services.
 */
interface CloudBridge : RemoteAccessible {

    /**
     * Map of clients. Key - client id, Value - service uuid.
     */
    @get:Throws(RemoteException::class)
    val clients: Map<Int, UUID>

    /**
     * Configure server for messaging.
     */
    @Throws(RemoteException::class)
    fun configure(server: Server)

}