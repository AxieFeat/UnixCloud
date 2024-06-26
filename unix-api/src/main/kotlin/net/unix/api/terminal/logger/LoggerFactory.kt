package net.unix.api.terminal.logger

interface LoggerFactory {

    /**
     * Получения глобального логгера
     */
    fun getLogger(): Logger

    /**
     * Получения логгера по его названию
     */
    fun getLogger(name: String): Logger
}