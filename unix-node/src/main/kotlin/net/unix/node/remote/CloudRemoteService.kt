package net.unix.node.remote

import net.unix.api.remote.RemoteAccessible
import net.unix.api.remote.RemoteService
import net.unix.node.CloudExtension.print
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.logging.CloudLogger
import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

object CloudRemoteService : RemoteService {

    private val toRegister = mutableMapOf<String, RemoteAccessible>()

    override fun register(remoteAccessible: RemoteAccessible, additional: String) {
        toRegister["${remoteAccessible.javaClass.name}${if(additional != "") "-$additional" else ""}"] = remoteAccessible
    }

    override fun start() {
        val port = UnixConfiguration.bridge.rmiPort

        LocateRegistry.createRegistry(port)

        toRegister.forEach { remoteAccessible ->
            Naming.rebind("//localhost/${remoteAccessible.key}", UnicastRemoteObject.exportObject(remoteAccessible.value, 0))
        }

        CloudLogger.info("RMI server started with ${toRegister.size} objects in $port")
        toRegister.forEach {
            CloudLogger.debug(" - ${it.key}")
        }
    }

}