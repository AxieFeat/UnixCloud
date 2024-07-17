package net.unix.cloud.terminal.logger

import net.unix.api.*
import net.unix.api.event.impl.cloud.CloudTerminalLoggerEvent
import net.unix.api.terminal.logger.LogType
import net.unix.api.terminal.logger.Logger
import net.unix.cloud.CloudExtension.parse
import net.unix.cloud.CloudExtension.parseColor
import net.unix.cloud.CloudExtension.stripColor
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.text.SimpleDateFormat

class LoggerImpl(
    override val prefix: String
) : Logger {

    companion object {
        private const val FORMAT_WITH_NAME = "&7 {1} &8| &7{3}/{2}&8 » &7{4}"
        private const val FORMAT_WITHOUT_NAME = "&7 {1} &8| &7{2}&8 » &7{4}"
        private val dateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        private fun formatTime(): String = dateFormat.format(System.currentTimeMillis())
    }

    override fun info(message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage, finalPrefix, finalLoggerName, LogType.INFO)
    }

    override fun error(message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage, finalPrefix, finalLoggerName, LogType.ERROR)
    }

    override fun warn(message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage, finalPrefix, finalLoggerName, LogType.WARN)
    }

    private fun printMessage(message: String, format: Boolean, loggerPrefix: String, logType: LogType) {
        val terminal = CloudAPI.instance.terminal

        if (!format) {
            terminal.print(message.parseColor())

            return
        }

        if (loggerPrefix != "") {
            val formatted = FORMAT_WITH_NAME
                .parse(
                    formatTime(), logType, loggerPrefix, "$message&r"
                )

            terminal.print(formatted.parseColor())

            LogManager.getLogger("info").log(Level.getLevel(logType.name), formatted.stripColor())

            return
        }

        val formatted = FORMAT_WITHOUT_NAME
            .parse(
                formatTime(), logType, loggerPrefix, "$message&r"
            )

        terminal.print(formatted.parseColor())

        LogManager.getLogger("info").log(Level.getLevel(logType.name), formatted.stripColor())
    }


}