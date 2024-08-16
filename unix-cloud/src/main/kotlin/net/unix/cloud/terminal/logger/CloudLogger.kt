package net.unix.cloud.terminal.logger

import net.kyori.adventure.text.Component
import net.unix.api.CloudAPI
import net.unix.api.CloudExtension.deserializeComponent
import net.unix.api.CloudExtension.format
import net.unix.api.CloudExtension.or
import net.unix.api.CloudExtension.rem
import net.unix.api.CloudExtension.strip
import net.unix.api.event.impl.cloud.CloudTerminalLoggerEvent
import net.unix.api.scheduler.scheduler
import net.unix.api.terminal.logger.LogType
import net.unix.api.terminal.logger.Logger
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.text.SimpleDateFormat

open class CloudLogger(
    override val prefix: String
) : Logger {

    companion object {
        private const val FORMAT_WITH_NAME =
            "<gray> {1}</gray> <dark_gray>|</dark_gray> <gray>{2}/{3}</gray><dark_gray> »</dark_gray> <gray>{4}</gray><reset>"

        private const val FORMAT_WITHOUT_NAME =
            "<gray> {1}</gray> <dark_gray>|</dark_gray> <gray>{2}</gray><dark_gray> »</dark_gray> <gray>{4}</gray><reset>"

        private val dateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        private fun formatTime(): String = dateFormat.format(System.currentTimeMillis())
    }


    override fun info(vararg message: String, format: Boolean) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.INFO)
    }

    override fun info(vararg message: Component, format: Boolean) {
        printMessage(message, format, LogType.INFO)
    }


    override fun error(vararg message: String, format: Boolean) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.ERROR)
    }

    override fun error(vararg message: Component, format: Boolean) {
        printMessage(message, format, LogType.ERROR)
    }

    override fun warn(vararg message: String, format: Boolean) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.WARN)
    }

    override fun warn(vararg message: Component, format: Boolean) {
        printMessage(message, format, LogType.WARN)
    }

    private fun printMessage(message: Array<out Component>, format: Boolean, logType: LogType) {
        scheduler {
            val event = CloudTerminalLoggerEvent(this@CloudLogger.prefix, format, *message).callEvent()

            if (event.cancelled) return@scheduler

            val finalMessage = event.message
            val finalPrefix = event.loggerName
            val finalFormat = event.format

            execute {
                val terminal = CloudAPI.instance.terminal

                if (!finalFormat) {
                    message.forEach { terminal.print(it) }
                    return@execute
                }

                val formatted = mutableListOf<Component>()

                finalMessage.forEach { text ->

                    val result = (finalPrefix != "") %
                        FORMAT_WITH_NAME.format(formatTime(), logType, finalPrefix, text) or
                        FORMAT_WITHOUT_NAME.format(formatTime(), logType, finalPrefix, text)


                    formatted.add(result.deserializeComponent())
                }

                formatted.forEach {
                    terminal.print(it)

                    execute {
                        LogManager.getLogger("info").log(Level.getLevel(logType.name), it.strip())
                    }
                }
            }
        }
    }


}