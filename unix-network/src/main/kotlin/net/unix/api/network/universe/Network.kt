package net.unix.api.network.universe

import net.unix.api.network.client.Client
import net.unix.api.network.server.Server
import net.unix.api.network.universe.listener.Listener
import net.unix.api.network.universe.listener.PacketListener

/**
 * Network packet sender
 *
 * @see [Server]
 * @see [Client]
 */
interface Network {

    /**
     * Send packet to server
     *
     * @param packet Instance of [Packet]
     *
     * @return Current instance of [Network]
     *
     * @see [Server]
     * @see [Client]
     */
    fun sendPacket(packet: Packet): Network

    /**
     * Send packet to client
     *
     * @param id Client id
     * @param packet Instance of [Packet]
     *
     * @return Current instance of [Network]
     *
     * @see [Server]
     * @see [Client]
     */
    fun sendPacket(id: Int, packet: Packet): Network

    /**
     * Create listener
     *
     * @param channel Channel to listen
     * @param type Type of listener
     * @param listener Listener
     *
     * @return Current instance of [Network]
     *
     * @see [Server]
     * @see [Client]
     */
    fun createListener(channel: String, type: Listener.ListenerType = Listener.ListenerType.RECEIVED, listener: Listener): Network

    /**
     * Create listener
     *
     * @param channel Channel to listen
     * @param listener Listener
     *
     * @return Current instance of [Network]
     *
     * @see [Server]
     * @see [Client]
     */
    fun createListener(channel: String, listener: PacketListener): Network

    companion object
}