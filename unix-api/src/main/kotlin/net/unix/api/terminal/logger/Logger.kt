package net.unix.api.terminal.logger

/**
 * UnixCloud logger
 */
interface Logger {

    /**
     * Logger prefix
     */
    val prefix: String

    /**
     * Log info to terminal
     *
     * @param message Message
     * @param format Message formatting
     */
    fun info(message: String, format: Boolean = true)

    /**
     * Log error to terminal
     *
     * @param message Message
     * @param format Message formatting
     */
    fun error(message: String, format: Boolean = true)

    /**
     * Lag warn to terminal
     *
     * @param message Message
     * @param format Message formatting
     */
    fun warn(message: String, format: Boolean = true)
}