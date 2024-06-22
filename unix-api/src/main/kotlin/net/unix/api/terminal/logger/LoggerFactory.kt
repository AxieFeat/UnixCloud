package net.unix.api.terminal.logger

interface LoggerFactory {
    fun getLogger(): Logger
    fun getLogger(name: String): Logger
}