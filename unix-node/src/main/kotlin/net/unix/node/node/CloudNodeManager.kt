package net.unix.node.node

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.node.Node
import net.unix.api.node.NodeManager
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.logging.CloudLogger
import java.util.concurrent.CompletableFuture

object CloudNodeManager : NodeManager {

    private fun readResolve(): Any = CloudNodeManager

    override val nodes: Set<Node>
        get() {
            val completable = CompletableFuture<Set<Node>>()

            Packet.builder()
                .setChannel("fun:node:list")
                .onResponse { conn, packet ->
                    val nodes = packet.string ?: listOf()

                    completable.complete(nodes.map { CloudNode(client, it) }.toSet())
                }
                .onResponseTimeout(5000) {
                    completable.complete(setOf())
                }
                .send(client)

            return completable.join()
        }

    override val client: Client = Client()

    override fun configure(client: Client) {
        client.connect(UnixConfiguration.node.host, 8181)

        Packet.builder()
            .setChannel("fun:node:auth")
            .onResponse { conn, packet ->
                val fail = packet.get<String>("fail") ?: return@onResponse

                CloudLogger.severe(fail)
            }
            .onResponseTimeout(5000) {
                CloudLogger.severe("Can not connect to Center of nodes.")
            }
            .send(client)

        client.createListener("fun:node:startTime") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:startTime")
                .asResponseFor(packet)
                .addNamedLong("time" to ThisNode.startTime)
                .send(client)
        }

        client.createListener("fun:node:uptime") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:uptime")
                .asResponseFor(packet)
                .addNamedLong("time" to ThisNode.uptime)
                .send(client)
        }

        client.createListener("fun:node:usageMemory") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:usageMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.usageMemory)
                .send(client)
        }

        client.createListener("fun:node:freeMemory") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:freeMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.freeMemory)
                .send(client)
        }

        client.createListener("fun:node:maxMemory") { conn, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:maxMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.maxMemory)
                .send(client)
        }
    }

    override fun get(name: String): Node? = nodes.find { it.name == name }
}