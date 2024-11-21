@file:Suppress("MemberVisibilityCanBePrivate")

package net.unix.cloud.logging

import net.kyori.adventure.text.Component
import net.unix.cloud.CloudExtension.deserializeComponent
import net.unix.cloud.CloudExtension.format
import net.unix.cloud.CloudExtension.serialize
import net.unix.cloud.CloudExtension.strip
import net.unix.cloud.CloudInstance
import net.unix.cloud.configuration.UnixConfiguration
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object CloudLogger : Logger("UnixCloudLogger", null) {

    var debug = false

    private val format = UnixConfiguration.terminal.logger.format
    private val formatFile = UnixConfiguration.terminal.logger.formatFile
    private val dataFormat = SimpleDateFormat(UnixConfiguration.terminal.logger.dateFormat)

    private val logsDir = CloudInstance.instance.locationSpace.logs

    private val cacheSize = UnixConfiguration.terminal.logger.cacheSize
    private val cachedMessages = ArrayList<Pair<String, CloudLevel>>()

    init {
        level = Level.ALL
        useParentHandlers = false

        System.setProperty("java.util.logging.SimpleFormatter.format", "%5\$s %n")

        if (!logsDir.exists())
            logsDir.mkdirs()

        val simpleFormatter = SimpleFormatter()

        val fileHandler = FileHandler(logsDir.canonicalPath + "/${
            SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + "-%g.log"
        }", 5242880, 100, false)
        fileHandler.encoding = StandardCharsets.UTF_8.name()
        fileHandler.level = Level.ALL
        fileHandler.formatter = simpleFormatter

        addHandler(fileHandler)

        deleteOldLogs()
        GlobalUncaughtExceptionLogger.register()
    }

    private fun deleteOldLogs() {
        val allLogFiles = (logsDir.listFiles() ?: emptyArray()).filterNotNull()
        allLogFiles.filter { isOlderThanTenDays(it) }.forEach { it.delete() }
    }

    private fun isOlderThanTenDays(logFile: File): Boolean {
        val tenDaysInMillis = TimeUnit.DAYS.toMillis(10)
        return (System.currentTimeMillis() - logFile.lastModified()) > tenDaysInMillis
    }

    @Synchronized
    fun service(msg: String) {
        printMessage(msg, LogType.SERVICE)
    }

    @Synchronized
    fun service(msg: Component) {
        debug(msg.serialize())
    }

    @Synchronized
    fun debug(msg: String) {
        if(debug) printMessage(msg, LogType.DEBUG)
    }

    @Synchronized
    fun debug(msg: Component) {
        if(debug) debug(msg.serialize())
    }

    @Synchronized
    override fun info(msg: String) {
        printMessage(msg, LogType.INFO)
    }

    @Synchronized
    fun info(msg: Component) {
        info(msg.serialize())
    }

    @Synchronized
    override fun warning(msg: String) {
        printMessage(msg, LogType.WARN)
    }

    @Synchronized
    fun warning(msg: Component) {
        warning(msg.serialize())
    }

    @Synchronized
    override fun severe(msg: String) {
        printMessage(msg, LogType.ERROR)
    }

    @Synchronized
    fun severe(msg: Component) {
        severe(msg.serialize())
    }

    @Synchronized
    fun printCachedMessages() {
        cachedMessages.forEach {
            printMessage(it.first, it.second, false)
        }
    }

    private fun printMessage(msg: String, level: CloudLevel, cache: Boolean = false) {
        if (cache && level != LogType.WARN) {
            if (cachedMessages.size >= cacheSize) {
                cachedMessages.removeAt(0)
            }

            cachedMessages.add(Pair(msg, level))
        }

        val coloredMessage = formatString(msg, level)

        if (level == LogType.SERVICE) {
            super.log(level, msg)
            CloudInstance.instance.terminal.print(msg)
            return
        }

        super.log(
            level,
            formatFile.format(
                dataFormat.format(System.currentTimeMillis()),
                level.name,
                msg.deserializeComponent().strip()
            )
        )

        CloudInstance.instance.terminal.print(coloredMessage)
    }

    private fun formatString(text: String, type: CloudLevel): String {
        return format.format(dataFormat.format(System.currentTimeMillis()), type.name, text)
    }

    @Synchronized
    fun exception(cause: Throwable) {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        cause.printStackTrace(pw)
        val stackTraceMessage = "<red>" + sw.buffer.toString()
        this.severe(stackTraceMessage)
    }

}

object GlobalUncaughtExceptionLogger : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        CloudLogger.severe("<red>Uncaught exception in a thread ${t.name}:")
        CloudLogger.exception(e)
    }

    fun register() {
        Thread.setDefaultUncaughtExceptionHandler(GlobalUncaughtExceptionLogger)
    }
}