package net.unix.cloud.terminal.logger

import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory

class LoggerFactoryImpl : LoggerFactory {

    private var systemLogger: Logger? = null
    private val loggers = mutableMapOf<String, Logger>()

    override val logger: Logger
        get() {
            if (systemLogger == null) {
                this.systemLogger = LoggerImpl("")
            }

            return systemLogger!!
        }

    override fun getLogger(name: String): Logger {
        val logger = loggers[name]

        if (logger == null) {
            val loggerImpl = LoggerImpl(name)

            loggers[name] = loggerImpl

            return loggerImpl
        }

        return logger
    }
}