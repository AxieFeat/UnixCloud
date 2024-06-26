package net.unix.api.terminal.logger

interface Logger {

    /**
     * Префикс логгера
     */
    val prefix: String

    /**
     * Отправка обычного сообщения в терминал
     */
    fun info(message: String, format: Boolean = true)

    /**
     * Отправка ошибки в терминал
     */
    fun error(message: String, format: Boolean = true)

    /**
     * Отправка предупреждения в терминал
     */
    fun warn(message: String, format: Boolean = true)
}