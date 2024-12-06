@file:Suppress("NAME_SHADOWING")

package net.unix.center

import net.unix.api.network.server.Server
import net.unix.api.network.universe.Packet
import net.unix.api.network.universe.listener.Listener

object NodeHandler {

    private val nodes = mutableMapOf<String, Int>()

    private val server = Server()

    fun start(port: Int) {
        println("Starting node handler...")
        server.start(port)

        server.createListener("empty", Listener.ListenerType.CONNECTED) { conn, packet ->
            println("Connected client with id ${conn.id}")
        }

        server.createListener("fun:node:list") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:list")
                .asResponseFor(packet)
                .addString(*nodes.keys.toTypedArray())
                .send(conn.id, server)
        }

        server.createListener("fun:node:auth") { conn, packet ->
            if(packet == null) return@createListener

            val name = packet.get<String>("name") ?: return@createListener

            if(nodes[name] != null) {
                Packet.builder()
                    .setChannel("fun:node:auth")
                    .asResponseFor(packet)
                    .addNamedString("fail" to "Node with name $name already registered!")
                    .send(conn.id, server)

                return@createListener
            }

            nodes[name] = conn.id

            println("Registered node $name")
        }

        server.createListener("empty", Listener.ListenerType.DISCONNECTED) { conn, packet ->
            nodes.toMap().forEach {
                if(it.value == conn.id) {
                    nodes.remove(it.key)
                    println("Unregistered node ${it.key}")
                }
            }
        }

        server.createListener("fun:node:startTime") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:startTime")
                .onResponse { conn, packet ->

                    val time = packet.get<Long>("time") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:startTime")
                        .asResponseFor(requestPacket)
                        .addNamedLong("time" to time)
                        .send(requestConn.id, server)
                }
                .send(nodes[target] ?: return@createListener, server)
        }

        server.createListener("fun:node:uptime") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:uptime")
                .onResponse { conn, packet ->

                    val time = packet.get<Long>("time") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:uptime")
                        .asResponseFor(requestPacket)
                        .addNamedLong("time" to time)
                        .send(requestConn.id, server)
                }
                .send(nodes[target] ?: return@createListener, server)
        }

        server.createListener("fun:node:usageMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:usageMemory")
                .onResponse { conn, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:usageMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .send(requestConn.id, server)
                }
                .send(nodes[target] ?: return@createListener, server)
        }

        server.createListener("fun:node:freeMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:freeMemory")
                .onResponse { conn, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:freeMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .send(requestConn.id, server)
                }
                .send(nodes[target] ?: return@createListener, server)
        }

        server.createListener("fun:node:maxMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:maxMemory")
                .onResponse { conn, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:maxMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .send(requestConn.id, server)
                }
                .send(nodes[target] ?: return@createListener, server)
        }

        println("Started.")
    }

}