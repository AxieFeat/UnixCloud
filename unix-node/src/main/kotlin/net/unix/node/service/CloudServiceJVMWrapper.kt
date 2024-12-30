package net.unix.node.service

import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.api.service.wrapper.ConsoleCloudServiceWrapper
import net.unix.node.group.CloudJVMGroup
import net.unix.node.logging.CloudLogger
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import java.io.*

/**
 * Executor for JVM executable files.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class CloudServiceJVMWrapper(
    override val service: CloudService,
    override val executableFile: File = File(service.dataFolder, service.group.executableFile),
    val properties: List<String> =
        if(service.group is CloudJVMGroup)
            (service.group as CloudJVMGroup).properties.plus(executableFile.path)
        else
            listOf("java", "-Xms100M", "-Xmx1G", "-jar", executableFile.path)
) : AbstractCloudExecutableCloud(service, executableFile), ConsoleCloudServiceWrapper {

    /**
     * Builder for service process.
     */
    @Transient
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

            if (value && !field)
                logs.forEach { CloudLogger.service(it) }
        }

    /**
     * Service process.
     */
    @Transient
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
        service.status = CloudServiceStatus.PREPARED
        started = false
        process.destroy()
    }

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    override fun command(command: String) {
        scheduler {
            execute {
                val writer = PrintStream(process.outputStream)

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