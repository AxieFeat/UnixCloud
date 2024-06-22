package net.unix.cloud.command.sender

import net.unix.api.command.sender.ConsoleCommandSender
import net.unix.cloud.cloudLogger

class ConsoleCommandSenderImpl : ConsoleCommandSender {

    override val name: String = "CONSOLE"

    override fun sendMessage(message: String) {
        cloudLogger.info(message)
    }
}