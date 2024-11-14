package net.unix.api.service

import java.io.File

/**
 * Allow to start [CloudService].
 *
 * With this executable, you can run almost anything with any functionality for [CloudService]'s.
 */
interface CloudExecutable {

    /**
     * A [CloudService] that is managed.
     */
    val service: CloudService

    /**
     * Executable file.
     */
    val executableFile: File

    /**
     * Is [CloudExecutable] run.
     */
    var started: Boolean

    /**
     * Start [CloudService].
     */
    fun start()

    /**
     * Kill process.
     */
    fun kill()

    companion object
}