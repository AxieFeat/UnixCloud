package net.unix.api.network.server

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.minlog.Log
import net.unix.api.network.client.Client
import net.unix.api.network.universe.Network
import net.unix.api.network.universe.Packet
import net.unix.api.network.universe.ResponsePacket
import net.unix.api.network.universe.listener.Listener
import net.unix.api.network.universe.listener.PacketListener

/**
 * Network server
 */
open class Server : com.esotericsoftware.kryonet.Server(), Network {

    val waitingPacketListener = WaitingPacketListener()

    init {
        Log.set(6)
    }

    companion object

    /**
     * Register class for Kryo serializer
     *
     * @param clazz Class to register
     *
     * @return Current instance of [Client]
     */
    fun registerClass(vararg clazz: Class<*>): Server {

        val kryo = super.getKryo()

        clazz.forEach {
            kryo.register(it)
        }

        return this
    }

    /**
     * Start server on specific port
     *
     * @param port Server port
     *
     * @return Current instance of [Server]
     */
    fun start(port: Int): Server {
        start()

        registerClass(
            String::class.java,
            Packet::class.java,
            Any::class.java,
            ArrayList::class.java,
            List::class.java,
            LinkedHashMap::class.java
        )

        bind(port, port)

        super.addListener(waitingPacketListener)

        return this
    }

    override fun createListener(channel: String, type: Listener.ListenerType, listener: Listener): Server {
        super.addListener(
            object : com.esotericsoftware.kryonet.Listener() {
                override fun received(connection: Connection, content: Any) {
                    if (content !is Packet) return

                    if (content.channel == channel && type == Listener.ListenerType.RECEIVED) {
                        listener.run(connection, content)
                    }
                }

                override fun connected(connection: Connection) {
                    if(type == Listener.ListenerType.CONNECTED) listener.run(connection, null)
                }

                override fun disconnected(connection: Connection) {
                    if(type == Listener.ListenerType.DISCONNECTED) listener.run(connection, null)
                }

                override fun idle(connection: Connection) {
                    if(type == Listener.ListenerType.IDLE) listener.run(connection, null)
                }
            }
        )

        return this
    }

    override fun createListener(channel: String, listener: PacketListener): Server {
        super.addListener(
            object : com.esotericsoftware.kryonet.Listener() {
                override fun received(connection: Connection, content: Any) {
                    if (content !is Packet) return

                    if (content.channel == channel) {
                        listener.receive(connection, content)
                    }
                }

                override fun connected(connection: Connection) {
                    listener.connected(connection)
                }

                override fun disconnected(connection: Connection) {
                    listener.disconnected(connection)
                }

                override fun idle(connection: Connection) {
                    listener.idle(connection)
                }
            }
        )

        return this
    }

    override fun sendPacket(packet: Packet): Server {
        super.sendToAllTCP(packet)

        return this
    }

    override fun sendPacket(id: Int, packet: Packet): Network {
        super.sendToTCP(id, packet)

        return this
    }

    class WaitingPacketListener : com.esotericsoftware.kryonet.Listener() {

        val waitingPackets = mutableListOf<Pair<String, ResponsePacket>>()

        override fun received(conn: Connection?, content: Any?) {
            if (content !is Packet) return

            val uuid = content.uuid

            waitingPackets.toList().forEach {
                if (it.first == uuid) {
                    it.second.run(conn!!, content)
                    waitingPackets.remove(it)
                }
            }
        }
    }
}