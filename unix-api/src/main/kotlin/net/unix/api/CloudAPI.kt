package net.unix.api

import net.unix.api.chimera.server.Server
import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.module.CloudModuleManager
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.JLineTerminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory
import java.io.File

/**
 * UnixCloud API
 */
abstract class CloudAPI {

    companion object {
        /**
         * API instance
         */
        lateinit var instance: CloudAPI
    }

    init {
        instance = this
    }

    /**
     * UnixCloud directory
     */
    abstract val mainDirectory: File

    /**
     * UnixCloud terminal
     */
    abstract val terminal: JLineTerminal

    /**
     * UnixCloud logger
     */
    abstract val logger: Logger

    /**
     * UnixCloud logger factory
     */
    abstract val loggerFactory: LoggerFactory

    /**
     * UnixCloud command dispatcher
     */
    abstract val commandDispatcher: CommandDispatcher

    /**
     * UnixCloud cloud service manager
     */
    abstract val cloudServiceManager: CloudServiceManager

    /**
     * UnixCloud cloud template manager
     */
    abstract val cloudTemplateManager: CloudTemplateManager

    /**
     * UnixCloud cloud group manager
     */
    abstract val cloudGroupManager: CloudGroupManager

    /**
     * UnixCloud module manager
     */
    abstract val moduleManager: CloudModuleManager

    /**
     * Manager for scheduler
     *
     * For INTERNAL usage only!
     */
    abstract val schedulerManager: SchedulerManager

    /**
     * Websocket server on port 9191
     */
    abstract val server: Server

    /**
     * Shutdown UnixCloud
     */
    abstract fun shutdown()
}