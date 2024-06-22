package net.unix.api.terminal.logger

interface Logger {

    val prefix: String

    fun info(message: String, format: Boolean = true)
    fun error(message: String, format: Boolean = true)
    fun warn(message: String, format: Boolean = true)
}