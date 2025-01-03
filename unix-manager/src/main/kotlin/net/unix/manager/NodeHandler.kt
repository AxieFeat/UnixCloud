package net.unix.manager

import net.unix.api.network.server.Server
import net.unix.api.network.universe.Packet
import net.unix.api.network.universe.listener.Listener

object NodeHandler {

    private val nodes = mutableMapOf<String, Int>()

    private val server = Server()

    fun start(port: Int) {
        println("Starting node handler...")
        server.start(port)

        server.createListener("empty", Listener.ListenerType.CONNECTED) { conn, _ ->
            println("Connected client with id ${conn.id}")
        }

        server.createListener("fun:node:list") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:list")
                .asResponseFor(packet)
                .addString(*nodes.keys.toTypedArray())
                .sendBy(server to conn.id)
        }

        server.createListener("fun:node:auth") { conn, packet ->
            if(packet == null) return@createListener

            val name = packet.get<String>("name") ?: return@createListener

            if(nodes[name] != null) {
                Packet.builder()
                    .setChannel("fun:node:auth")
                    .asResponseFor(packet)
                    .addNamedString("fail" to "Node with name $name already registered!")
                    .sendBy(server to conn.id)

                return@createListener
            }

            nodes[name] = conn.id

            println("Registered node $name")
        }

        server.createListener("empty", Listener.ListenerType.DISCONNECTED) { conn, _ ->
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
                .onResponse { _, packet ->

                    val time = packet.get<Long>("time") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:startTime")
                        .asResponseFor(requestPacket)
                        .addNamedLong("time" to time)
                        .sendBy(server to requestConn.id)
                }
                .sendBy(server to (nodes[target] ?: return@createListener))
        }

        server.createListener("fun:node:uptime") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:uptime")
                .onResponse { _, packet ->

                    val time = packet.get<Long>("time") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:uptime")
                        .asResponseFor(requestPacket)
                        .addNamedLong("time" to time)
                        .sendBy(server to requestConn.id)
                }
                .sendBy(server to (nodes[target] ?: return@createListener))
        }

        server.createListener("fun:node:usageMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:usageMemory")
                .onResponse { _, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:usageMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .sendBy(server to requestConn.id)
                }
                .sendBy(server to (nodes[target] ?: return@createListener))
        }

        server.createListener("fun:node:freeMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:freeMemory")
                .onResponse { _, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:freeMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .sendBy(server to requestConn.id)
                }
                .sendBy(server to (nodes[target] ?: return@createListener))
        }

        server.createListener("fun:node:maxMemory") { requestConn, requestPacket ->
            if(requestPacket == null) return@createListener

            val target = requestPacket.get<String>("name") ?: return@createListener

            Packet.builder()
                .setChannel("fun:node:maxMemory")
                .onResponse { _, packet ->

                    val memory = packet.get<Long>("memory") ?: return@onResponse

                    Packet.builder()
                        .setChannel("fun:node:maxMemory")
                        .asResponseFor(requestPacket)
                        .addNamedLong("memory" to memory)
                        .sendBy(server to requestConn.id)
                }
                .sendBy(server to (nodes[target] ?: return@createListener))
        }

        println("Started.")
    }

}