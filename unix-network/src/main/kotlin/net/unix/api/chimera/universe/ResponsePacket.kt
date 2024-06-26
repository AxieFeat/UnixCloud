package net.unix.api.chimera.universe

import com.esotericsoftware.kryonet.Connection

fun interface ResponsePacket {
    fun run(connection: Connection, packet: Packet)

    companion object
}