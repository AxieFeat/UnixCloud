package net.unix.node.node

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.node.Node
import net.unix.api.node.NodeManager
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.logging.CloudLogger
import net.unix.scheduler.impl.scheduler
import java.io.IOException

object CloudNodeManager : NodeManager {

    private fun readResolve(): Any = CloudNodeManager

    private var cachedNodes = mutableSetOf<Node>()

    override val nodes: Set<Node>
        get() = cachedNodes

    override val client: Client = Client()

    override fun configure(client: Client) {
        try {
            client.connect(UnixConfiguration.node.host, 8181)
        } catch (e: IOException) {
            CloudLogger.severe("Can not connect to Center of nodes.")
            cachedNodes.add(ThisNode)
            return
        }

        Packet.builder()
            .setChannel("fun:node:auth")
            .addNamedString("name" to ThisNode.name)
            .onResponse { _, packet ->
                val fail = packet.get<String>("fail") ?: return@onResponse

                CloudLogger.severe("Can not connect to Center of nodes.")
                CloudLogger.severe(fail)
            }
            .sendBy(client)

        client.createListener("fun:node:startTime") { _, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:startTime")
                .asResponseFor(packet)
                .addNamedLong("time" to ThisNode.startTime)
                .sendBy(client)
        }

        client.createListener("fun:node:uptime") { _, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:uptime")
                .asResponseFor(packet)
                .addNamedLong("time" to ThisNode.uptime)
                .sendBy(client)
        }

        client.createListener("fun:node:usageMemory") { _, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:usageMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.usageMemory)
                .sendBy(client)
        }

        client.createListener("fun:node:freeMemory") { _, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:freeMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.freeMemory)
                .sendBy(client)
        }

        client.createListener("fun:node:maxMemory") { _, packet ->
            if(packet == null) return@createListener

            Packet.builder()
                .setChannel("fun:node:maxMemory")
                .asResponseFor(packet)
                .addNamedLong("memory" to ThisNode.maxMemory)
                .sendBy(client)
        }

        scheduler {
            execute(0, 5000) {
                Packet.builder()
                    .setChannel("fun:node:list")
                    .onResponse { _, packet ->
                        val nodes = packet.string ?: listOf()

                        val result = mutableSetOf<Node>()

                        nodes.forEach {
                            if(ThisNode.name == it) {
                                result.add(ThisNode)
                            } else {
                                result.add(CloudNode(CloudNodeManager.client, it))
                            }
                        }

                        cachedNodes = result
                    }
                    .onResponseTimeout(5000) {
                        cachedNodes = mutableSetOf(ThisNode)
                    }
                    .sendBy(CloudNodeManager.client)
            }
        }
    }

    override fun get(name: String): Node? = nodes.find { it.name == name }
}