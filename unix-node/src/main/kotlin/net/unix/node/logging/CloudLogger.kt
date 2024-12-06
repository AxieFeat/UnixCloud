@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package net.unix.node.logging

import net.kyori.adventure.text.Component
import net.unix.api.LocationSpace
import net.unix.api.terminal.Terminal
import net.unix.node.CloudExtension.deserializeComponent
import net.unix.node.CloudExtension.format
import net.unix.node.CloudExtension.serialize
import net.unix.node.CloudExtension.strip
import net.unix.node.configuration.UnixConfiguration
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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

object CloudLogger : Logger("UnixCloudLogger", null), KoinComponent {

    private val locationSpace: LocationSpace by inject()
    private val terminal: Terminal by inject()

    var debug = false

    private val format = UnixConfiguration.terminal.logger.format
    private val formatFile = UnixConfiguration.terminal.logger.formatFile
    private val dataFormat = SimpleDateFormat(UnixConfiguration.terminal.logger.dateFormat)

    private val logsDir = locationSpace.logs

    private val cacheSize = UnixConfiguration.terminal.logger.cacheSize
    private val cachedMessages = ArrayList<Pair<String, CloudLevel>>()

    init {
        level = Level.ALL
        useParentHandlers = false

        System.setProperty("java.util.logging.SimpleFormatter.format", "%5\$s %n")

        if (!logsDir.exists())
            logsDir.mkdirs()

        val simpleFormatter = SimpleFormatter()

        val fileHandler = FileHandler(
            logsDir.canonicalPath + "/${
            SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + "-%g.log"
        }", 5242880, 100, false)
        fileHandler.encoding = StandardCharsets.UTF_8.name()
        fileHandler.level = Level.ALL
        fileHandler.formatter = simpleFormatter

        addHandler(fileHandler)

        System.setOut(UnixPrintStream)

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
    fun service(msg: String, vararg replace: Any) {
        printMessage(msg.format(replace), LogType.SERVICE)
    }

    @Synchronized
    fun service(msg: Component, vararg replace: Any) {
        debug(msg.serialize().format(replace))
    }

    @Synchronized
    fun debug(msg: String, vararg replace: Any) {
        if(debug) printMessage(msg.format(replace), LogType.DEBUG)
    }

    @Synchronized
    fun debug(msg: Component, vararg replace: Any) {
        if(debug) debug(msg.serialize().format(replace))
    }

    @Synchronized
    fun info(msg: String, vararg replace: Any) {
        printMessage(msg.format(replace), LogType.INFO)
    }

    @Synchronized
    fun info(msg: Component, vararg replace: Any) {
        info(msg.serialize().format(replace))
    }

    @Synchronized
    override fun info(msg: String) {
        printMessage(msg, LogType.INFO)
    }

    @Synchronized
    fun warning(msg: String, vararg replace: Any) {
        printMessage(msg.format(replace), LogType.WARN)
    }

    @Synchronized
    fun warning(msg: Component, vararg replace: Any) {
        warning(msg.serialize().format(replace))
    }

    @Synchronized
    override fun warning(msg: String) {
        printMessage(msg, LogType.WARN)
    }

    @Synchronized
    fun severe(msg: String, vararg replace: Any) {
        printMessage(msg.format(replace), LogType.ERROR)
    }

    @Synchronized
    fun severe(msg: Component, vararg replace: Any) {
        severe(msg.serialize().format(replace))
    }

    @Synchronized
    override fun severe(msg: String) {
        printMessage(msg, LogType.ERROR)
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
            terminal.print(msg)
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

        terminal.print(coloredMessage)
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
        severe(stackTraceMessage)
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