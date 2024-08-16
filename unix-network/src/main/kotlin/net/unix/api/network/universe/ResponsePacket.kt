package net.unix.api.network.universe

import com.esotericsoftware.kryonet.Connection

/**
 * Action on receiving response for packet
 *
 * @see [Packet.PacketBuilder.onResponse]
 */
fun interface ResponsePacket {

    /**
     * Run code
     *
     * @param connection Instance of [Connection]
     * @param packet Received [Packet]
     */
    fun run(connection: Connection, packet: Packet)

    companion object
}