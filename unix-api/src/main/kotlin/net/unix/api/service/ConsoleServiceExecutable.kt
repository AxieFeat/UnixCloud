package net.unix.api.service

interface ConsoleServiceExecutable : ServiceExecutable {

    /**
     * All service terminal logs.
     */
    val logs: MutableList<String>

    /**
     * Is console view of service enabled?
     */
    var viewConsole: Boolean

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    fun command(command: String)

}