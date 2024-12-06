@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.driver

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.LocationSpace
import net.unix.api.group.CloudGroupManager
import net.unix.api.group.SaveableCloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceInfo
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.driver.persistence.RemotePersistenceDataType
import net.unix.node.CloudExtension.readJson
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.File
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry

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

    val info: CloudServiceInfo = run {
        val file = File(dataFolder, "service.info")

        if(!file.exists()) throw IllegalArgumentException("Can not find info file in service!")

        return@run CloudServiceInfo.deserialize(file.readJson())
    }

    lateinit var service: CloudService

    fun install() {
        println("Service instance installing...")

        val registry = LocateRegistry.getRegistry("localhost", 1099)
        val locationSpace = registry.lookup("net.unix.cloud.CloudLocationSpace") as LocationSpace
        val cloudTemplateManager = registry.lookup("net.unix.cloud.template.BasicCloudTemplateManager") as CloudTemplateManager
        val cloudGroupManager = registry.lookup("net.unix.cloud.group.CloudJVMGroupManager") as SaveableCloudGroupManager
        val cloudServiceManager = registry.lookup("net.unix.cloud.service.CloudJVMServiceManager") as CloudServiceManager
        val moduleManager = registry.lookup("net.unix.cloud.modification.module.CloudModuleManager") as ModuleManager
        val extensionManager = registry.lookup("net.unix.cloud.modification.extension.CloudExtensionManager") as ExtensionManager

        startKoin {
            val module = module {
                single<Registry> { registry }
                single<LocationSpace> { locationSpace }
                single<CloudTemplateManager> { cloudTemplateManager }
                single<CloudGroupManager> { cloudGroupManager }
                single<CloudServiceManager> { cloudServiceManager }
                single<RemotePersistenceDataType> { RemotePersistenceDataType }
                single<ModuleManager> { moduleManager }
                single<ExtensionManager> { extensionManager }
            }

            modules(module)
        }

        this.service = cloudServiceManager[info.uuid] ?: throw IllegalArgumentException("Cant find current CloudService instance!")

        val client = Client()
        client.connect("0.0.0.0", 9191)

        println("Service connected to node!")

        Packet.builder()
            .setChannel("fun:service:auth")
            .addNamedString("uuid" to service.uuid.toString())
            .send(client)

        println("Service started.")

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
            }
        }
    }

}