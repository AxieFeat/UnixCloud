package net.unix.cloud.service

import net.unix.api.service.AbstractCloudExecutable
import net.unix.api.service.CloudService
import net.unix.api.service.ConsoleServiceExecutable
import net.unix.cloud.group.CloudJVMGroup
import net.unix.cloud.logging.CloudLogger
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import java.io.*

/**
 * Executor for JVM executable files.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class ServiceJVMExecutable(
    override val service: CloudService,
    override val executableFile: File = File(service.dataFolder, service.group.executableFile),
    val properties: List<String> =
        if(service.group is CloudJVMGroup)
            (service.group as CloudJVMGroup).properties.plus(executableFile.path)
        else
            listOf("java", "-Xms100M", "-Xmx1G", "-jar", executableFile.path)
) : AbstractCloudExecutable(service, executableFile), ConsoleServiceExecutable {

    /**
     * Builder for service process.
     */
    val processBuilder = run {
        val process = ProcessBuilder(properties)

        process.directory(service.dataFolder)
        process.redirectErrorStream(true)

        return@run process
    }

    override val logs = mutableListOf<String>()

    override var viewConsole: Boolean = false
        set(value) {
            field = value

            if (value)
                logs.forEach { CloudLogger.service(it) }
        }

    /**
     * Service process.
     */
    lateinit var process: Process

    override fun start() {
        scheduler(SchedulerType.EXECUTOR) {
            execute {
                CloudLogger.info("Trying to start ${service.name}...")
                process = processBuilder.start()
                started = true

                startLogging()
            }
        }
    }

    override fun kill() {
        CloudLogger.info("Service ${service.name} killed")
        started = false
        process.destroy()
    }

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    override fun command(command: String) {
        scheduler(SchedulerType.EXECUTOR) {
            execute {
                val writer = PrintWriter(process.outputStream)

                writer.println(command)
                writer.flush()
            }
        }
    }

    private fun startLogging() {
        scheduler(SchedulerType.EXECUTOR) {
            execute {
                val reader = BufferedReader(InputStreamReader(process.inputStream, "UTF-8"))

                var line: String?

                while ((reader.readLine().also { line = it }) != null) {
                    logs.add(line!!)
                    if(viewConsole) CloudLogger.service(line!!)
                }
            }
        }
    }
}