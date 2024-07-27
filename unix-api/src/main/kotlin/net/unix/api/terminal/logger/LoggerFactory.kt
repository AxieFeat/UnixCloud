package net.unix.api.terminal.logger

/**
 * Factory for creation instances of [Logger]
 */
interface LoggerFactory {

    /**
     * Global logger
     */
    val logger: Logger

    /**
     * Get logger by name
     *
     * @param name Logger name
     *
     * @return Instance of [Logger]
     */
    fun getLogger(name: String): Logger

    companion object
}