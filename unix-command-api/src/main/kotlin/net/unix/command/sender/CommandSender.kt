package net.unix.command.sender

import net.kyori.adventure.text.Component

/**
 * General representation of the command sender.
 */
interface CommandSender {

    /**
     * Sender name.
     */
    val name: String

    /**
     * Send message to command sender.
     *
     * @param message Message text.
     */
    fun sendMessage(message: String)

    /**
     * Send message to command sender.
     *
     * @param message Component to send.
     */
    fun sendMessage(message: Component)

}