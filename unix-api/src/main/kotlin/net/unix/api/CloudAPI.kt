package net.unix.api

import net.unix.api.command.CommandDispatcher
import net.unix.api.module.CloudModuleManager
import net.unix.api.service.ServiceManager
import net.unix.api.terminal.JLineTerminal

/**
 * Класс, представляющий API UnixCloud
 */
abstract class CloudAPI {

    companion object {
        lateinit var instance: CloudAPI
    }

    init {
        instance = this
    }

    abstract val terminal: JLineTerminal
    abstract val commandDispatcher: CommandDispatcher
    abstract val serviceManager: ServiceManager
    abstract val moduleManager: CloudModuleManager
}