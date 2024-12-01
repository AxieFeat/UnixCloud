package net.unix.cloud.remote

import net.unix.api.remote.RemoteAccessible
import net.unix.api.remote.RemoteService
import net.unix.cloud.logging.CloudLogger
import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

object CloudRemoteService : RemoteService {

    private val toRegister = mutableMapOf<String, RemoteAccessible>()

    override fun register(remoteAccessible: RemoteAccessible, additional: String) {
        toRegister["${remoteAccessible.javaClass.name}${if(additional != "") "-$additional" else ""}"] = remoteAccessible
    }

    override fun start() {
        LocateRegistry.createRegistry(1099)
        toRegister.forEach { remoteAccessible ->
            Naming.rebind("//localhost/${remoteAccessible.key}", UnicastRemoteObject.exportObject(remoteAccessible.value, 0))
        }
        CloudLogger.info("RMI server started with ${toRegister.size} objects")
        toRegister.forEach {
            CloudLogger.debug(" - ${it.key}")
        }
    }

}