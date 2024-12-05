package net.unix.node.command.sender

import net.kyori.adventure.text.Component
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.logging.CloudLogger
import net.unix.command.sender.ConsoleCommandSender

class CloudConsoleCommandSender : ConsoleCommandSender {

    override val name: String = UnixConfiguration.terminal.consoleName

    override fun sendMessage(message: String) {
        CloudLogger.info(message)
    }

    override fun sendMessage(message: Component) {
        CloudLogger.info(message)
    }
}