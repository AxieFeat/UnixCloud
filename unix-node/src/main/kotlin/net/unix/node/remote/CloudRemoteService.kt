package net.unix.node.remote

import net.unix.api.remote.RemoteAccessible
import net.unix.api.remote.RemoteService
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.logging.CloudLogger
import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import kotlin.reflect.KClass

object CloudRemoteService : RemoteService {

    private val toRegister = mutableMapOf<String, RemoteAccessible>()

    override fun register(type: KClass<*>, remoteAccessible: RemoteAccessible, additional: String) {
        toRegister["${type.simpleName}${if(additional != "") "-$additional" else ""}"] = remoteAccessible
    }

    override fun start() {
        val port = UnixConfiguration.bridge.rmiPort

        LocateRegistry.createRegistry(port)

        toRegister.forEach { remoteAccessible ->
            Naming.rebind(remoteAccessible.key, UnicastRemoteObject.exportObject(remoteAccessible.value, 0))
        }

        CloudLogger.info("RMI server started with ${toRegister.size} objects in port $port")
        toRegister.forEach {
            CloudLogger.debug(" - ${it.key}")
        }
    }

}