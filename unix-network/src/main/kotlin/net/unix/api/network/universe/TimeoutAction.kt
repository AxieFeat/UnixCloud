package net.unix.api.network.universe

/**
 * Action for packet timeout
 *
 * @see [Packet.PacketBuilder.onResponseTimeout]
 */
fun interface TimeoutAction {

    /**
     * Run code
     */
    fun run()

    companion object
}