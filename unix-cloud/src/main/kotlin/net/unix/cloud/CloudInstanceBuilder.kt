//package net.unix.cloud
//
//import net.unix.api.CloudAPI
//import net.unix.api.CloudBuilder
//import net.unix.api.LocationSpace
//import net.unix.api.bridge.CloudBridge
//import net.unix.api.command.CommandDispatcher
//import net.unix.api.group.CloudGroupManager
//import net.unix.api.modification.extension.ExtensionManager
//import net.unix.api.modification.module.ModuleManager
//import net.unix.api.network.server.Server
//import net.unix.api.service.CloudServiceManager
//import net.unix.api.template.CloudTemplateManager
//import net.unix.api.terminal.Terminal
//
//@Suppress("unused")
//class CloudInstanceBuilder : CloudBuilder {
//
//    private var locationSpace: LocationSpace? = null
//
//    private var commandDispatcher: CommandDispatcher? = null
//    private var terminal: Terminal? = null
//
//    private var cloudTemplateManager: CloudTemplateManager? = null
//    private var cloudGroupManager: CloudGroupManager? = null
//    private var cloudServiceManager: CloudServiceManager? = null
//
//    private var moduleManager: ModuleManager? = null
//    private var extensionManager: ExtensionManager? = null
//
//    private var server: Server? = null
//
//    private var bridge: CloudBridge? = null
//
//    companion object {
//        fun CloudAPI.Companion.builder(): CloudInstanceBuilder {
//            return CloudInstanceBuilder()
//        }
//    }
//
//    override fun terminal(terminal: Terminal): CloudInstanceBuilder {
//        this.terminal = terminal
//
//        return this
//    }
//
//    override fun commandDispatcher(dispatcher: CommandDispatcher): CloudInstanceBuilder {
//        this.commandDispatcher = dispatcher
//
//        return this
//    }
//
//    override fun commandTemplateManager(manager: CloudTemplateManager): CloudInstanceBuilder {
//        this.cloudTemplateManager = manager
//
//        return this
//    }
//
//    override fun cloudGroupManager(manager: CloudGroupManager): CloudInstanceBuilder {
//        this.cloudGroupManager = manager
//
//        return this
//    }
//
//    override fun cloudServiceManager(manager: CloudServiceManager): CloudInstanceBuilder {
//        this.cloudServiceManager = manager
//
//        return this
//    }
//
//    override fun moduleManager(manager: ModuleManager): CloudInstanceBuilder {
//        this.moduleManager = manager
//
//        return this
//    }
//
//    override fun extensionManager(manager: ExtensionManager): CloudInstanceBuilder {
//        this.extensionManager = manager
//
//        return this
//    }
//
//    override fun server(server: Server): CloudInstanceBuilder {
//        this.server = server
//
//        return this
//    }
//
//    override fun bridge(bridge: CloudBridge): CloudBuilder {
//        this.bridge = bridge
//
//        return this
//    }
//
//    override fun locationSpace(space: LocationSpace): CloudInstanceBuilder {
//        this.locationSpace = space
//
//        return this
//    }
//
//    override fun build(): CloudInstance {
//        return CloudInstance(
//            locationSpace,
//            commandDispatcher,
//            terminal,
//            cloudTemplateManager,
//            cloudGroupManager,
//            cloudServiceManager,
//            moduleManager,
//            extensionManager,
//            server,
//            bridge
//        )
//    }
//}