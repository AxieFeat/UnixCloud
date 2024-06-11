package net.unix.cloud.command.sender

import net.unix.api.command.sender.ConsoleCommandSender
import java.util.logging.Logger

class ConsoleCommandSenderImpl : ConsoleCommandSender {

    override val name: String = "CONSOLE"

    override fun sendMessage(message: String) {
        Logger.getGlobal().info(message)
    }
}