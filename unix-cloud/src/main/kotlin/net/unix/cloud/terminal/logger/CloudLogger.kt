package net.unix.cloud.terminal.logger

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.unix.cloud.event.cloud.CloudTerminalLoggerEvent
import net.unix.api.terminal.logger.LogType
import net.unix.api.terminal.logger.Logger
import net.unix.cloud.CloudExtension.deserializeComponent
import net.unix.cloud.CloudExtension.format
import net.unix.cloud.CloudExtension.or
import net.unix.cloud.CloudExtension.rem
import net.unix.cloud.CloudExtension.strip
import net.unix.cloud.CloudInstance
import net.unix.cloud.event.callEvent
import net.unix.cloud.scheduler.scheduler
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


    override fun info(vararg message: String, format: Boolean, throwable: Throwable?) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.INFO, throwable)
    }

    override fun info(vararg message: Component, format: Boolean, throwable: Throwable?) {
        printMessage(message, format, LogType.INFO, throwable)
    }


    override fun error(vararg message: String, format: Boolean, throwable: Throwable?) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.ERROR, throwable)
    }

    override fun error(vararg message: Component, format: Boolean, throwable: Throwable?) {
        printMessage(message, format, LogType.ERROR, throwable)
    }

    override fun warn(vararg message: String, format: Boolean, throwable: Throwable?) {
        val components = message.map { it.deserializeComponent() }.toTypedArray()

        printMessage(components, format, LogType.WARN, throwable)
    }

    override fun warn(vararg message: Component, format: Boolean, throwable: Throwable?) {
        printMessage(message, format, LogType.WARN, throwable)
    }

    private fun printMessage(message: Array<out Component>, format: Boolean, logType: LogType, throwable: Throwable?) {
        scheduler {
            val event = CloudTerminalLoggerEvent(this@CloudLogger.prefix, format, *message, throwable = throwable).callEvent()

            if (event.cancelled) return@scheduler

            val finalMessage = event.message
            val finalPrefix = event.loggerName
            val finalFormat = event.format
            val finalThrowable = event.throwable

            execute {
                val terminal = CloudInstance.instance.terminal

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

                finalThrowable?.stackTrace?.forEach {

                    val text = Component.text().color(NamedTextColor.RED).append(Component.text(it.toString())).resetStyle().build()

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