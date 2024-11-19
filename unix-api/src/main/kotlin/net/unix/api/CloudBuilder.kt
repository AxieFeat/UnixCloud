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

/**
 * Builder for Cloud API.
 */
interface CloudBuilder : Builder<CloudAPI> {

    /**
     * Set terminal.
     *
     * @param terminal Terminal.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun terminal(terminal: Terminal): CloudBuilder

    /**
     * Set command dispatcher.
     *
     * @param dispatcher Command dispatcher.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun commandDispatcher(dispatcher: CommandDispatcher): CloudBuilder

    /**
     * Set template manager.
     *
     * @param manager Template manager.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun commandTemplateManager(manager: CloudTemplateManager): CloudBuilder

    /**
     * Set group manager.
     *
     * @param manager Group manager.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun cloudGroupManager(manager: CloudGroupManager): CloudBuilder

    /**
     * Set service manager.
     *
     * @param manager Service manager.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun cloudServiceManager(manager: CloudServiceManager): CloudBuilder

    /**
     * Set module manager.
     *
     * @param manager Module manager.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun moduleManager(manager: ModuleManager): CloudBuilder

    /**
     * Set extension manager.
     *
     * @param manager Extension manager.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun extensionManager(manager: ExtensionManager): CloudBuilder

    /**
     * Set server.
     *
     * @param server Server.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun server(server: Server): CloudBuilder

    /**
     * Set location space.
     *
     * @param space Location space.
     *
     * @return Current instance of [CloudBuilder].
     */
    fun locationSpace(space: LocationSpace): CloudBuilder

}