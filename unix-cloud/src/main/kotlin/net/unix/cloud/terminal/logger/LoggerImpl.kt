package net.unix.cloud.terminal.logger

import net.unix.api.*
import net.unix.api.CloudExtension.parse
import net.unix.api.CloudExtension.parseColor
import net.unix.api.CloudExtension.stripColor
import net.unix.api.event.impl.cloud.CloudTerminalLoggerEvent
import net.unix.api.scheduler.Scheduler.scheduler
import net.unix.api.terminal.logger.LogType
import net.unix.api.terminal.logger.Logger
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

    override fun info(vararg message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, *message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage.toList(), finalPrefix, finalLoggerName, LogType.INFO)
    }

    override fun error(vararg message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, *message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage.toList(), finalPrefix, finalLoggerName, LogType.ERROR)
    }

    override fun warn(vararg message: String, format: Boolean) {
        val event = CloudTerminalLoggerEvent(this.prefix, format, *message).callEvent()

        if (event.cancelled) {
            return
        }

        val finalLoggerName = event.loggerName
        val finalMessage = event.message
        val finalPrefix = event.format

        printMessage(finalMessage.toList(), finalPrefix, finalLoggerName, LogType.WARN)
    }

    private fun printMessage(message: List<String>, format: Boolean, loggerPrefix: String, logType: LogType) {
        scheduler {
            execute {
                val terminal = CloudAPI.instance.terminal

                if (!format) {
                    val parsed = message.map { it.parseColor() }

                    parsed.forEach {
                        terminal.print(it)
                    }

                    return@execute
                }

                val formatted = mutableListOf<String>()
                val withoutColors = mutableListOf<String>()

                message.forEach { text ->
                    val result = if (loggerPrefix != "") {
                        FORMAT_WITH_NAME.parse(
                            formatTime(), logType, loggerPrefix, "$text&r"
                        )
                    } else {
                        FORMAT_WITHOUT_NAME
                            .parse(
                                formatTime(), logType, loggerPrefix, "$text&r"
                            )
                    }

                    formatted.add(result.parseColor())
                    withoutColors.add(result.stripColor())
                }

                execute {
                    formatted.forEach {
                        terminal.print(it)
                    }
                }

                execute {
                    withoutColors.forEach {
                        LogManager.getLogger("info").log(Level.getLevel(logType.name), it)
                    }
                }
            }
        }
    }


}