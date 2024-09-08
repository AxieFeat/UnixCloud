package net.unix.api

import net.unix.api.network.server.Server
import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory
import net.unix.api.service.CloudService
import net.unix.api.group.CloudGroup
import net.unix.api.template.CloudTemplate
import net.unix.api.modification.module.CloudModule

/**
 * Unix API.
 */
abstract class CloudAPI {

    /**
     * Location space, that's contains some files.
     */
    abstract val locationSpace: LocationSpace

    /**
     * Terminal, just a terminal.
     */
    abstract val terminal: Terminal

    /**
     * Terminal logger.
     */
    abstract val logger: Logger

    /**
     * Factory for creation loggers.
     */
    abstract val loggerFactory: LoggerFactory

    /**
     * Command dispatcher.
     */
    abstract val commandDispatcher: CommandDispatcher

    /**
     * Manager to control [CloudService]'s.
     */
    abstract val cloudServiceManager: CloudServiceManager

    /**
     * Manager to control [CloudTemplate]'s.
     */
    abstract val cloudTemplateManager: CloudTemplateManager

    /**
     * Manager to control [CloudGroup]'s.
     */
    abstract val cloudGroupManager: CloudGroupManager

    /**
     * Manager to control [CloudModule]'s.
     */
    abstract val moduleManager: ModuleManager

    /**
     * Manager to control [ExtensionManager]'s.
     */
    abstract val extensionManager: ExtensionManager

    /**
     * Manager for scheduler.
     *
     * For INTERNAL usage only!
     */
    abstract val schedulerManager: SchedulerManager

    /**
     * Websocket server on port 9191.
     */
    abstract val server: Server

    /**
     * Shutdown.
     */
    abstract fun shutdown()
}