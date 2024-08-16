package net.unix.api.service

import java.io.File

/**
 * Allow to start [CloudService]
 */
interface CloudExecutable {

    /**
     * A [CloudService] that is managed
     */
    val service: CloudService

    /**
     * Executable file
     */
    val executableFile: File

    /**
     * Is [CloudExecutable] run
     */
    var started: Boolean

    /**
     * Start [CloudService]
     */
    fun start()

    /**
     * Stop process
     */
    fun stop()

    /**
     * Kill process
     */
    fun kill()

    /**
     * Create a copy of current [CloudExecutable] for other [CloudService]
     */
    fun copy(service: CloudService): CloudExecutable

    companion object
}