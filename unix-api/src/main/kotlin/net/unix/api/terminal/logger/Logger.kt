package net.unix.api.terminal.logger

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

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
     * @param format Enable message formatting?
     * @param message Message to log. It will be deserialized to [Component] by [MiniMessage]
     */
    fun info(vararg message: String, format: Boolean = true)

    /**
     * Log info to terminal
     *
     * @param format Enable message formatting?
     * @param message Message to log
     */
    fun info(vararg message: Component, format: Boolean = true)

    /**
     * Log error to terminal
     *
     * @param format Enable message formatting?
     * @param message Message to log. It will be deserialized to [Component] by [MiniMessage]
     */
    fun error(vararg message: String, format: Boolean = true)

    /**
     * Log error to terminal
     *
     * @param format Enable message formatting?
     * @param message Message to log
     */
    fun error(vararg message: Component, format: Boolean = true)

    /**
     * Lag warn to terminal
     *
     * @param format Enable message formatting?
     * @param message Message to log
     */
    fun warn(vararg message: String, format: Boolean = true)

    /**
     * Lag warn to terminal
     *
     * @param format Enable message formatting?
     * @param message Message to log. It will be deserialized to [Component] by [MiniMessage]
     */
    fun warn(vararg message: Component, format: Boolean = true)

    companion object
}