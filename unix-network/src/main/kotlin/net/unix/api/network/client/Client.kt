package net.unix.api.network.client

import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.minlog.Log
import net.unix.api.network.server.Server
import net.unix.api.network.universe.Network
import net.unix.api.network.universe.Packet
import net.unix.api.network.universe.ResponsePacket
import net.unix.api.network.universe.file.*
import net.unix.api.network.universe.listener.Listener
import net.unix.api.network.universe.listener.PacketListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Network client
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class Client : com.esotericsoftware.kryonet.Client(), Network {

    // TODO crypto
    //val keyPair = CryptoUtil.generateKeyPair()
    val waitingPacketListener = WaitingPacketListener()
    val fileListener = FileListener(this)

    init {
        Log.set(6)
    }

    companion object

    /**
     * Register class for Kryo serializer.
     *
     * @param clazz Class to register.
     *
     * @return Current instance of [Client].
     */
    fun registerClass(vararg clazz: Class<*>): Client {
        val kryo = super.getKryo()

        clazz.forEach {
            kryo.register(it)
        }

        return this
    }

    /**
     * Connect to some server.
     *
     * @param host Host to connect.
     * @param port Port to connect.
     *
     * @return Current instance of [Client].
     */
    fun connect(host: String, port: Int): Client {
        start()

        registerClass(
            String::class.java,
            Packet::class.java,
            Any::class.java,
            ArrayList::class.java,
            List::class.java,
            LinkedHashMap::class.java,
            Set::class.java,
            FileRequest::class.java,
            FileTransferChunk::class.java,
            ProgressUpdate::class.java,
            ByteArray::class.java
        )

        connect(5000, host, port, port)

        super.addListener(waitingPacketListener)
        super.addListener(fileListener)

        return this
    }

    override fun createListener(channel: String, type: Listener.ListenerType, listener: Listener): Client {
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

     override fun createListener(channel: String, listener: PacketListener): Client {
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
        return sendObject(packet)
    }

    override fun sendPacket(id: Int, packet: Packet): Network {
        throw RuntimeException("Don't use Client#sendPacket() with this params, server only!")
    }

    override fun sendObject(any: Any): Client {
        super.sendTCP(any)

        return this
    }

    override fun sendObject(id: Int, any: Any): Client {
        throw RuntimeException("Don't use Client#sendObject() with this params, server only!")
    }

    class FileListener(val client: Client) : com.esotericsoftware.kryonet.Listener() {

        val requestedFiles = mutableMapOf<String, RequestFile>()
        val aliveFiles = mutableMapOf<String, ProgressUpdate>()

        override fun received(conn: Connection, content: Any?) {

            if(content is ProgressUpdate) {
                if(aliveFiles[content.uuid] != null) {
                    aliveFiles[content.uuid!!] = content
                    requestedFiles[content.uuid]?.progressTask?.run(content)
                }
            }

            if(content is FileTransferChunk) {
                writeToFile(content.saveOn!!, content.bytes)

                if(content.last) {
                    requestedFiles[content.uuid]?.onSuccess?.call()

                    aliveFiles.remove(content.uuid)
                    requestedFiles.remove(content.uuid)
                    return
                }

                if(!aliveFiles.contains(content.uuid))
                    aliveFiles[content.uuid!!] = ProgressUpdate(content.uuid, 0, 0)
            }

            if(content is FileRequest) {
                FileTransfer.send()
                    .fromRequest(content)
                    .sendBy(client)
            }
        }

        private fun writeToFile(path: String, bytes: ByteArray) {
            try {
                FileOutputStream(path, true).use { fos ->
                    fos.write(bytes)
                }
            } catch (e: IOException) {
                println("Error writing to file")
                throw e
            }
        }
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