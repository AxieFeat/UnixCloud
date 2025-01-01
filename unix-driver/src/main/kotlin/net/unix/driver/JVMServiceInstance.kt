@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.driver

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.LocationSpace
import net.unix.api.group.GroupManager
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.Service
import net.unix.api.service.ServiceInfo
import net.unix.api.service.ServiceManager
import net.unix.api.service.ServiceStatus
import net.unix.api.template.TemplateManager
import net.unix.driver.persistence.RemotePersistenceDataType
import net.unix.node.CloudExtension.readJson
import net.unix.node.service.JVMServiceInfo
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
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

    lateinit var info: ServiceInfo

    lateinit var service: Service

    fun install(port: Int = 9191, rmiPort: Int = 1099) {
        println("Service instance installing...")

        val registry = LocateRegistry.getRegistry("localhost", rmiPort)
        val locationSpace = registry.find<LocationSpace>()
        val templateManager = registry.find<TemplateManager>()
        val groupWrapperFactoryManager = registry.find<GroupWrapperFactoryManager>()
        val groupManager = registry.find<GroupManager>()
        val serviceManager = registry.find<ServiceManager>()
        val moduleManager = registry.find<ModuleManager>()
        val extensionManager = registry.find<ExtensionManager>()

        startKoin {
            val module = module {
                single<Registry>(named("default")) { registry }
                single<LocationSpace>(named("default")) { locationSpace }
                single<TemplateManager>(named("default")) { templateManager }
                single<GroupManager>(named("default")) { groupManager }
                single<GroupWrapperFactoryManager>(named("default")) { groupWrapperFactoryManager }
                single<ServiceManager>(named("default")) { serviceManager }
                single<RemotePersistenceDataType>(named("default")) { RemotePersistenceDataType }
                single<ModuleManager>(named("default")) { moduleManager }
                single<ExtensionManager>(named("default")) { extensionManager }
            }

            modules(module)
        }

        val file = File(dataFolder, "service.info")

        if(!file.exists()) throw IllegalArgumentException("Can not find info file in service!")

        this.info = JVMServiceInfo.deserialize(file.readJson())
        this.service = serviceManager[info.uuid] ?: throw IllegalArgumentException("Cant find current CloudService instance!")

        this.service.status = ServiceStatus.STARTED

        val client = Client()
        client.connect("0.0.0.0", port)

        println("Service connected to node!")

        Packet.builder()
            .setChannel("fun:service:auth")
            .addNamedString("uuid" to service.uuid.toString())
            .sendBy(client)

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
                        .sendBy(client)
                }

                execute {
                    Packet.builder()
                        .setChannel("fun:service:memory:used")
                        .addNamedLong("memory" to used)
                        .sendBy(client)
                }

                execute {
                    Packet.builder()
                        .setChannel("fun:service:memory:free")
                        .addNamedLong("memory" to free)
                        .sendBy(client)
                }
            }
        }
    }

    private inline fun <reified T> Registry.find(): T {
        return this.lookup(T::class.simpleName) as T
    }

}