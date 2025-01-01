package net.unix.api.service.wrapper

import net.unix.api.pattern.Killable
import net.unix.api.pattern.Startable
import net.unix.api.remote.RemoteAccessible
import net.unix.api.service.Service
import java.io.File
import java.rmi.RemoteException

/**
 * Allow to start [Service].
 *
 * With this wrapper, you can run almost anything with any functionality for [Service]'s.
 */
interface ServiceWrapper : Killable, Startable, RemoteAccessible {

    /**
     * A [Service] that is managed.
     */
    @get:Throws(RemoteException::class)
    val service: Service

    /**
     * Executable file.
     */
    @get:Throws(RemoteException::class)
    val executableFile: File

    /**
     * Is [ServiceWrapper] run.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var running: Boolean

    /**
     * Start [Service].
     */
    @Throws(RemoteException::class)
    override fun start()

    /**
     * Kill process.
     */
    @Throws(RemoteException::class)
    override fun kill()

    companion object
}