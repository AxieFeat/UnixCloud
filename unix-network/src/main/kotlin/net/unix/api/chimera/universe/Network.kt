package net.unix.api.chimera.universe

interface Network {
    fun sendPacket(packet: Packet): Network
    fun sendPacket(id: Int, packet: Packet): Network
}