package net.unix.command.sender

import net.kyori.adventure.audience.Audience

/**
 * General representation of the command sender.
 */
interface CommandSender : Audience {

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

}