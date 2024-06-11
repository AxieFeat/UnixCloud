package net.unix.api.command.sender

interface CommandSender {

    /**
     * Имя отправителя
     */
    val name: String

    /**
     * Отправка сообщения
     */
    fun sendMessage(message: String)
}