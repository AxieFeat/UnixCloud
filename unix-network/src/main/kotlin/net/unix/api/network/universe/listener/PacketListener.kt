package net.unix.api.network.universe.listener

import com.esotericsoftware.kryonet.Connection
import net.unix.api.network.client.Client
import net.unix.api.network.server.Server
import net.unix.api.network.universe.Packet

/**
 * Simple packet listener.
 *
 * @see [Server]
 * @see [Client]
 */
interface PacketListener {

    companion object

    /**
     * On connection.
     *
     * @param connection Instance of [Connection].
     *
     * @see [Server]
     * @see [Client]
     */
    fun connected(connection: Connection)

    /**
     * On disconnection.
     *
     * @param connection Instance of [Connection].
     *
     * @see [Server]
     * @see [Client]
     */
    fun disconnected(connection: Connection)

    /**
     * On idle.
     *
     * @param connection Instance of [Connection].
     *
     * @see [Server]
     * @see [Client]
     */
    fun idle(connection: Connection)

    /**
     * On packet receive.
     *
     * @param connection Instance of [Connection].
     * @param packet Received [Packet].
     *
     * @see [Server]
     * @see [Client]
     */
    fun receive(connection: Connection, packet: Packet)
}