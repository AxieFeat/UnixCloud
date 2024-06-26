package net.unix.api.chimera.client

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.minlog.Log
import net.unix.api.chimera.server.Server
import net.unix.api.chimera.universe.Packet
import net.unix.api.chimera.universe.listener.PacketListener
import net.unix.api.chimera.universe.Network
import net.unix.api.chimera.universe.ResponsePacket
import net.unix.api.chimera.universe.listener.Listener
import java.util.*

class Client : com.esotericsoftware.kryonet.Client(), Network {

    val waitingPacketListener = WaitingPacketListener()

    init {
        Log.set(6)
    }

    companion object

    fun registerClass(vararg clazz: Class<*>): Client {
        val kryo = super.getKryo()

        clazz.forEach {
            kryo.register(it)
        }

        return this
    }

    fun connect(host: String, port: Int): Client {
        start()

        registerClass(
            String::class.java,
            Packet::class.java,
            Any::class.java,
            ArrayList::class.java,
            List::class.java,
            LinkedHashMap::class.java
        )

        connect(5000, host, port, port)

        super.addListener(waitingPacketListener)

        return this
    }


    fun createListener(channel: String, type: Listener.ListenerType = Listener.ListenerType.RECEIVED, listener: Listener): Client {
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

     fun createListener(channel: String, listener: PacketListener): Client {
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

    override fun sendPacket(packet: Packet): Client {
        sendTCP(packet)

        return this
    }

    override fun sendPacket(id: Int, packet: Packet): Network {
        throw RuntimeException("Don't use Client#sendPacket() with this params, server only!")
    }

    class WaitingPacketListener() : com.esotericsoftware.kryonet.Listener() {

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