package net.unix.cloud.service

import net.unix.api.scheduler.SchedulerType
import net.unix.api.service.CloudExecutable
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceStatus
import net.unix.cloud.scheduler.scheduler
import java.io.*

/**
 * Executor for JVM executable files.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class CloudJVMExecutable(
    override val service: CloudService,
    override val executableFile: File = File(service.dataFolder, "service.jar"),
    properties: List<String>
) : CloudExecutable {

    override var started: Boolean = false
        set(value) {
            field = value

            if (value) service.status = CloudServiceStatus.STARTED
            else service.status = CloudServiceStatus.PREPARED
        }

    /**
     * Builder for service process.
     */
    val processBuilder = run {
        val process = ProcessBuilder(properties)

        process.directory(executableFile.parentFile)
        process.redirectErrorStream(true)

        return@run process
    }

    /**
     * All service terminal logs.
     */
    val logs = mutableListOf<String>()

    /**
     * Service process.
     */
    lateinit var process: Process

    override fun start() {
        scheduler(SchedulerType.EXECUTOR) {
            execute {
                process = processBuilder.start()
                started = true

                startLogging()
            }
        }
    }

    override fun kill() {
        started = false
        process.destroy()
    }

    /**
     * Send console command to the service.
     *
     * @param command Command to send.
     */
    fun command(command: String) {
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
                }
            }
        }
    }
}