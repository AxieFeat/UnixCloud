package net.unix.cloud.command.sender

import net.kyori.adventure.text.Component
import net.unix.api.command.sender.ConsoleCommandSender
import net.unix.api.terminal.logger.Logger

class CloudConsoleCommandSender(
    private val logger: Logger
) : ConsoleCommandSender {

    override val name: String = "CONSOLE"

    override fun sendMessage(message: String) {
        logger.info(message)
    }

    override fun sendMessage(message: Component) {
        logger.info(message)
    }
}