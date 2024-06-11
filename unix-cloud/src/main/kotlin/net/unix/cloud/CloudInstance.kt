package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.command.CommandDispatcher
import net.unix.api.module.CloudModuleManager
import net.unix.api.service.ServiceManager
import net.unix.api.terminal.JLineTerminal
import net.unix.cloud.command.CommandDispatcherImpl
import net.unix.cloud.module.CloudModuleManagerImpl
import net.unix.cloud.service.ServiceManagerImpl
import net.unix.cloud.terminal.JLineTerminalImpl

fun main() {
    CloudInstance
}

object CloudInstance : CloudAPI() {
    override val commandDispatcher: CommandDispatcher = CommandDispatcherImpl
    override val serviceManager: ServiceManager = ServiceManagerImpl
    override val moduleManager: CloudModuleManager = CloudModuleManagerImpl
    override val terminal: JLineTerminal = JLineTerminalImpl("> ")
}