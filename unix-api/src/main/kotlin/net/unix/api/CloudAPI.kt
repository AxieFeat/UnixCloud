package net.unix.api

import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.module.CloudModuleManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.JLineTerminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory
import java.io.File

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

    abstract val mainDirectory: File
    abstract val terminal: JLineTerminal

    abstract val logger: Logger
    abstract val loggerFactory: LoggerFactory

    abstract val commandDispatcher: CommandDispatcher

    abstract val cloudServiceManager: CloudServiceManager
    abstract val cloudTemplateManager: CloudTemplateManager
    abstract val cloudGroupManager: CloudGroupManager
    abstract val moduleManager: CloudModuleManager
}