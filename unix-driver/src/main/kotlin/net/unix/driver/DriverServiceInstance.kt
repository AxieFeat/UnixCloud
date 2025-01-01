@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.driver

import net.unix.api.network.client.Client
import net.unix.api.network.universe.Packet
import net.unix.api.service.Service
import net.unix.api.service.ServiceInfo
import net.unix.api.service.ServiceStatus
import net.unix.node.CloudExtension.readJson
import net.unix.node.service.JVMServiceInfo
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import java.io.File

class DriverServiceInstance(
    val dataFolder: File = run {
        val file = File(
            DriverServiceInstance::class.java.getProtectionDomain().codeSource.location.toURI()
        ).parentFile

        if (!file.exists()) {
            file.mkdirs()
        }

        return@run file
    },
    host: String = "localhost",
    val bridgePort: Int = 9191,
    rmiPort: Int = 1099
) : DriverInstance(host, rmiPort) {

    lateinit var info: ServiceInfo
    lateinit var service: Service

    override fun install() {
        super.install()

        //startKoin {
          //  val module = module {
//                single<Registry>(named("default")) { registry }
                //single<LocationSpace>(named("default")) { locationSpace }
//                single<RemotePersistenceDataType>(named("default")) { RemotePersistenceDataType }
//                single<TemplateManager>(named("default")) { templateManager }
//                single<ServiceManager>(named("default")) { serviceManager }
                //single<GroupManager>(named("default")) { groupManager }
                //single<GroupWrapperFactoryManager>(named("default")) { groupWrapperFactoryManager }
                //single<ModuleManager>(named("default")) { moduleManager }
                //single<ExtensionManager>(named("default")) { extensionManager }
           // }

            //modules(module)
       // }

        val file = File(dataFolder, "service.info")

        if(!file.exists()) throw IllegalArgumentException("Can not find info file in service!")

        this.info = JVMServiceInfo.deserialize(file.readJson())
        this.service = serviceManager[info.uuid] ?: throw IllegalArgumentException("Cant find current Service instance!")

        this.service.status = ServiceStatus.STARTED

        val client = Client()
        client.connect("0.0.0.0", bridgePort)

        println("Driver connected to node!")

        Packet.builder()
            .setChannel("fun:service:auth")
            .addNamedString("uuid" to service.uuid.toString())
            .sendBy(client)

        println("Service authorized.")

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

}