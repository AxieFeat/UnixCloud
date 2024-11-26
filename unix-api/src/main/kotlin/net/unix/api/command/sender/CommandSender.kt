package net.unix.api.command.sender

import net.kyori.adventure.text.Component
import net.unix.api.pattern.Nameable

/**
 * General representation of the command sender.
 */
interface CommandSender : Nameable {

    /**
     * Sender name.
     */
    override val name: String

    /**
     * Send message to command sender.
     *
     * @param message Message text.
     */
    fun sendMessage(message: String)

    /**
     * Send message to command sender.
     *
     * @param message Component to sent.
     */
    fun sendMessage(message: Component)

}