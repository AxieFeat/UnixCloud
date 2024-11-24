@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.backend

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import java.io.File
import java.util.UUID

object JVMServiceInstance {

    val dataFolder: File = run {
        val file = File(
            JVMServiceInstance::class.java.getProtectionDomain().codeSource.location.toURI()
        ).parentFile

        if (!file.exists()) {
            file.mkdirs()
        }

        return@run file
    }

    val uuid: UUID = UUID.fromString(dataFolder.name)

    fun install() {
        val client = Client()
        client.connect("0.0.0.0", 9191)

        Packet.builder()
            .setChannel("fun:service:auth")
            .addNamedString("uuid" to uuid.toString())
            .send(client)

        scheduler(SchedulerType.EXECUTOR) {
            execute(0, 1000) {

                val max = Runtime.getRuntime().maxMemory()
                val used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                val free = max - used

                execute {
                    Packet.builder()
                        .setChannel("fun:service:memory:max")
                        .addNamedLong("memory" to max)
                        .send(client)
                }

                execute {
                    Packet.builder()
                        .setChannel("fun:service:memory:used")
                        .addNamedLong("memory" to used)
                        .send(client)
                }

                execute {
                    Packet.builder()
                        .setChannel("fun:service:memory:free")
                        .addNamedLong("memory" to free)
                        .send(client)
                }

                println("Memory usage updated!")
            }
        }
    }
}