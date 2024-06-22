package net.unix.api.chimera.universe.listener

import com.esotericsoftware.kryonet.Connection
import net.unix.api.chimera.universe.Packet

interface PacketListener {
    fun connected(connection: Connection)
    fun disconnected(connection: Connection)
    fun idle(connection: Connection)
    fun receive(connection: Connection, packet: Packet)
}