package net.unix.api.service

import net.unix.api.pattern.Killable
import net.unix.api.pattern.Startable
import java.io.File

/**
 * Allow to start [CloudService].
 *
 * With this executable, you can run almost anything with any functionality for [CloudService]'s.
 */
interface ServiceExecutable : Killable, Startable {

    /**
     * A [CloudService] that is managed.
     */
    val service: CloudService

    /**
     * Executable file.
     */
    val executableFile: File

    /**
     * Is [ServiceExecutable] run.
     */
    var started: Boolean

    /**
     * Start [CloudService].
     */
    override fun start()

    /**
     * Kill process.
     */
    override fun kill()

    companion object
}