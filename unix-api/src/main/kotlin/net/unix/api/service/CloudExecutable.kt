package net.unix.api.service

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Allow to start [CloudService] with own parameters
 */
interface CloudExecutable {

    /**
     * Path to executable file
     */
    val executableFile: File

    /**
     * Run start process
     */
    fun run()

    /**
     * Stop process
     */
    fun stop()

    /**
     * Kill process
     */
    fun kill()

    companion object
}