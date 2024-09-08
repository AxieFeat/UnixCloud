package net.unix.api

import net.unix.api.builder.Builder
import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory

interface CloudBuilder : Builder<CloudAPI> {
    fun loggerFactory(factory: LoggerFactory): CloudBuilder

    fun globalLogger(logger: Logger): CloudBuilder

    fun terminal(terminal: Terminal): CloudBuilder

    fun commandDispatcher(dispatcher: CommandDispatcher): CloudBuilder

    fun commandTemplateManager(manager: CloudTemplateManager): CloudBuilder

    fun cloudGroupManager(manager: CloudGroupManager): CloudBuilder

    fun cloudServiceManager(manager: CloudServiceManager): CloudBuilder

    fun moduleManager(manager: ModuleManager): CloudBuilder

    fun extensionManager(manager: ExtensionManager): CloudBuilder

    fun schedulerManager(manager: SchedulerManager): CloudBuilder

    fun server(server: Server): CloudBuilder

    fun locationSpace(space: LocationSpace): CloudBuilder
}