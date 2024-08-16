package net.unix.cloud.terminal.logger

import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory

open class CloudLoggerFactory : LoggerFactory {

    private val loggers = mutableMapOf<String, Logger>()

    override val logger: Logger = CloudLogger("")


    override operator fun get(name: String): Logger {
        val logger = loggers[name]

        if (logger == null) {
            val cloudLogger = CloudLogger(name)

            loggers[name] = cloudLogger

            return cloudLogger
        }

        return logger
    }
}