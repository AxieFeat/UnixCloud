package net.unix.cloud.service

import net.unix.api.service.CloudExecutable
import net.unix.api.service.CloudService
import java.io.File

/**
 * Executor for JVM executable files.
 */
open class CloudJVMExecutable(
    override val service: CloudService,
    override val executableFile: File
) : CloudExecutable {

    override var started: Boolean = false

    override fun start() {

    }

    override fun stop() {

    }

    override fun kill() {

    }

    override fun copy(): CloudExecutable {
        TODO()
    }
}