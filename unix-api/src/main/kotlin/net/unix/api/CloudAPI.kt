package net.unix.api

import net.unix.api.bridge.CloudBridge
import net.unix.api.network.server.Server
import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.api.service.CloudService
import net.unix.api.group.CloudGroup
import net.unix.api.template.CloudTemplate
import net.unix.api.modification.module.Module

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
     * Manager to control [Module]'s.
     */
    abstract val moduleManager: ModuleManager

    /**
     * Manager to control [ExtensionManager]'s.
     */
    abstract val extensionManager: ExtensionManager

    /**
     * Websocket server on port 9191.
     */
    abstract val server: Server

    /**
     * Unix bridge for messaging.
     */
    abstract val bridge: CloudBridge

    /**
     * Shutdown.
     */
    abstract fun shutdown()

    companion object
}