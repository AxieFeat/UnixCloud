package net.unix.api.service.wrapper

import java.rmi.RemoteException

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
    val stopCommand: String

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    @Throws(RemoteException::class)
    fun command(command: String)

}