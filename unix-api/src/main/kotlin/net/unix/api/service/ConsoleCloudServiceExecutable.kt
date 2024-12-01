package net.unix.api.service

import java.rmi.RemoteException

interface ConsoleCloudServiceExecutable : CloudServiceExecutable {

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
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    @Throws(RemoteException::class)
    fun command(command: String)

}