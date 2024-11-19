package net.unix.cloud.command.sender

import net.kyori.adventure.text.Component
import net.unix.api.command.sender.ConsoleCommandSender
import net.unix.cloud.configuration.UnixConfiguration

class CloudConsoleCommandSender : ConsoleCommandSender {

    override val name: String = UnixConfiguration.terminal.consoleName

    override fun sendMessage(message: String) {
       // logger.info(message)
    }

    override fun sendMessage(message: Component) {
      //  logger.info(message)
    }
}