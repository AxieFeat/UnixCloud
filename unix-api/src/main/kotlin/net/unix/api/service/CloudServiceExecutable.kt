package net.unix.api.service

import net.unix.api.pattern.Killable
import net.unix.api.pattern.Startable
import net.unix.api.remote.RemoteAccessible
import java.io.File
import java.rmi.RemoteException

/**
 * Allow to start [CloudService].
 *
 * With this executable, you can run almost anything with any functionality for [CloudService]'s.
 */
interface CloudServiceExecutable : Killable, Startable, RemoteAccessible {

    /**
     * A [CloudService] that is managed.
     */
    @get:Throws(RemoteException::class)
    val service: CloudService

    /**
     * Executable file.
     */
    @get:Throws(RemoteException::class)
    val executableFile: File

    /**
     * Is [CloudServiceExecutable] run.
     */
    @get:Throws(RemoteException::class)
    @set:Throws(RemoteException::class)
    var started: Boolean

    /**
     * Start [CloudService].
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