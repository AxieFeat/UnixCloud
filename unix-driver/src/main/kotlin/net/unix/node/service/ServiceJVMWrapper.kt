package net.unix.node.service

import net.unix.api.service.Service
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.node.group.wrapper.GroupJVMWrapper
import java.io.File
import java.util.concurrent.CompletableFuture

@Suppress("unused")
open class ServiceJVMWrapper(
    override val service: Service,
    override val executableFile: File,
    override val startedLine: String,
    override val stopCommand: String,
    val properties: List<String> =
        if(service.group.wrapper is GroupJVMWrapper)
            (service.group.wrapper as GroupJVMWrapper).startProperties.plus(executableFile.path)
        else
            listOf("java", "-Xms100M", "-Xmx1G", "-jar", executableFile.path),
) : ConsoleServiceWrapper {

    override val logs: MutableList<String> = mutableListOf()

    override var viewConsole: Boolean = true

    override fun stop(): CompletableFuture<Unit> {
        TODO()
    }

    override fun command(command: String) {

    }

    override var running: Boolean = true

    override fun start() {
    }

    override fun kill() {
    }

    companion object {
        @JvmStatic
        private val serialVersionUID = 1049193751045838910L
    }
}