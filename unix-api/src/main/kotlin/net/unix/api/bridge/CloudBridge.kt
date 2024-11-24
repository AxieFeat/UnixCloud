package net.unix.api.bridge

import net.unix.api.network.server.Server
import java.util.*

/**
 * Represents bridge for messaging unix and services.
 */
interface CloudBridge {

    /**
     * Map of clients. Key - client id, Value - service uuid.
     */
    val clients: Map<Int, UUID>

    /**
     * Configure server for messaging.
     */
    fun configure(server: Server)

}