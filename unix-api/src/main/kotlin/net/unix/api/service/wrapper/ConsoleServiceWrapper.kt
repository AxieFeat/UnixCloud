package net.unix.api.service.wrapper

import java.rmi.RemoteException
import java.util.concurrent.CompletableFuture

/**
 * This interface represent a console wrapper. It means this wrapper for console applications.
 */
interface ConsoleServiceWrapper : ServiceWrapper {

    /**
     * All service terminal logs.
     */
    @get:Throws(RemoteException::class)
    val logs: MutableList<String>

    /**
     * Is console view of service enabled?
     */
    @get:Throws(RemoteException::class)
    var viewConsole: Boolean

    /**
     * Stop command for service.
     */
    @get:Throws(RemoteException::class)
    val stopCommand: String

    /**
     * Trigger-string, the occurrence of which will consider the service started
     */
    @get:Throws(RemoteException::class)
    val startedLine: String

    /**
     * Try stop service via [stopCommand].
     */
    @Throws(RemoteException::class)
    fun stop(): CompletableFuture<Unit>

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    @Throws(RemoteException::class)
    fun command(command: String)

}