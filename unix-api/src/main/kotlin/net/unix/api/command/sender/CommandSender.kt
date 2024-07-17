package net.unix.api.command.sender

/**
 * General representation of the command sender
 */
interface CommandSender {

    /**
     * Sender name
     */
    val name: String

    /**
     * Send message to command sender
     *
     * @param message Message text
     */
    fun sendMessage(message: String)
}