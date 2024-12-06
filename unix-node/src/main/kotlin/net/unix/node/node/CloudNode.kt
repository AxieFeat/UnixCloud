package net.unix.node.node

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.node.Node
import net.unix.scheduler.impl.scheduler
import java.util.concurrent.CompletableFuture

data class CloudNode(
    val client: Client,
    override val name: String
) : Node {

    override val startTime: Long
        get() {
            val completable = CompletableFuture<Long>()

            scheduler {
                execute {
                    Packet.builder()
                        .setChannel("fun:node:startTime")
                        .addNamedString("name" to name)
                        .onResponse { conn, packet ->
                            val time = packet.get<Long>("time")

                            completable.complete(time)
                        }
                        .onResponseTimeout(1000) {
                            completable.complete(0)
                        }
                        .send(client)
                }
            }

            return completable.join()
        }

    override val uptime: Long
        get() {
            val completable = CompletableFuture<Long>()

            scheduler {
                execute {
                    Packet.builder()
                        .setChannel("fun:node:uptime")
                        .addNamedString("name" to name)
                        .onResponse { conn, packet ->
                            val time = packet.get<Long>("time")

                            completable.complete(time)
                        }
                        .onResponseTimeout(1000) {
                            completable.complete(0)
                        }
                        .send(client)
                }
            }

            return completable.join()
        }

    override val usageMemory: Long
        get() {
            val completable = CompletableFuture<Long>()

            scheduler {
                execute {
                    Packet.builder()
                        .setChannel("fun:node:usageMemory")
                        .addNamedString("name" to name)
                        .onResponse { conn, packet ->
                            val memory = packet.get<Long>("memory")

                            completable.complete(memory)
                        }
                        .onResponseTimeout(1000) {
                            completable.complete(0)
                        }
                        .send(client)
                }
            }

            return completable.join()
        }

    override val freeMemory: Long
        get() {
            val completable = CompletableFuture<Long>()

            scheduler {
                execute {
                    Packet.builder()
                        .setChannel("fun:node:freeMemory")
                        .addNamedString("name" to name)
                        .onResponse { conn, packet ->
                            val memory = packet.get<Long>("memory")

                            completable.complete(memory)
                        }
                        .onResponseTimeout(1000) {
                            completable.complete(0)
                        }
                        .send(client)
                }
            }

            return completable.join()
        }

    override val maxMemory: Long
        get() {
            val completable = CompletableFuture<Long>()

            scheduler {
                execute {
                    Packet.builder()
                        .setChannel("fun:node:maxMemory")
                        .addNamedString("name" to name)
                        .onResponse { conn, packet ->
                            val memory = packet.get<Long>("memory")

                            completable.complete(memory)
                        }
                        .onResponseTimeout(1000) {
                            completable.complete(0)
                        }
                        .send(client)
                }
            }

            return completable.join()
        }

}