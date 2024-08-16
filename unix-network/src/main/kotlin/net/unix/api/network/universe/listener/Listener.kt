package net.unix.api.network.universe.listener

import com.esotericsoftware.kryonet.Connection
import net.unix.api.network.universe.Packet
import net.unix.api.network.client.Client
import net.unix.api.network.server.Server

/**
 * Simple packet listener
 *
 * @see [Server]
 * @see [Client]
 */
fun interface Listener {

    companion object

    /**
     * Run code
     *
     * @param connection Instance of [Connection]
     * @param packet Received [Packet]
     *
     * @see [Server]
     * @see [Client]
     */
    fun run(connection: Connection, packet: Packet?)

    enum class ListenerType {
        RECEIVED, CONNECTED, DISCONNECTED, IDLE
    }
}