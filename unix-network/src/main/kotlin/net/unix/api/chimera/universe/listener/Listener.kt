package net.unix.api.chimera.universe.listener

import com.esotericsoftware.kryonet.Connection
import net.unix.api.chimera.universe.Packet

fun interface Listener {

    companion object

    fun run(connection: Connection, packet: Packet?)

    enum class ListenerType {
        RECEIVED, CONNECTED, DISCONNECTED, IDLE
    }
}