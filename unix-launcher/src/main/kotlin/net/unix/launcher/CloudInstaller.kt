package net.unix.launcher

import net.unix.api.LocationSpace
import net.unix.api.ShutdownHandler
import net.unix.api.bridge.CloudBridge
import net.unix.api.group.GroupManager
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.node.NodeManager
import net.unix.api.remote.RemoteService
import net.unix.api.service.ServiceManager
import net.unix.api.template.TemplateManager
import net.unix.api.terminal.Terminal
import net.unix.command.CommandDispatcher
import net.unix.manager.NodeHandler
import net.unix.node.CloudInstance
import net.unix.node.CloudInstanceShutdownHandler
import net.unix.node.CloudLocationSpace
import net.unix.node.bridge.JVMBridge
import net.unix.node.command.CloudCommandDispatcher
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.event.callEvent
import net.unix.node.event.koin.KoinModuleRegisterEvent
import net.unix.node.event.koin.KoinStartEvent
import net.unix.node.group.JVMGroupManager
import net.unix.node.group.wrapper.CloudGroupWrapperFactoryManager
import net.unix.node.i18n.CloudI18nService
import net.unix.node.i18n.CloudSaveableLocaleManager
import net.unix.node.modification.extension.CloudExtensionManager
import net.unix.node.modification.module.CloudModuleManager
import net.unix.node.node.CloudNodeManager
import net.unix.node.remote.CloudRemoteService
import net.unix.node.service.JVMServiceManager
import net.unix.node.template.CloudTemplateManager
import net.unix.node.terminal.DefaultTerminal
import net.unix.node.terminal.JLineTerminal
import net.unix.node.terminal.unixUptime
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.Scanner

fun main(args: Array<String>) {

    if(args.isNotEmpty()) {

        var started = false

        if(args.contains("manager")) {
            NodeHandler.start(8181)
            started = true
        }
        if(args.contains("node")) {
            launchCloudInstance()
            started = true
        }

        if(started) return
    }

    when(System.getProperty("start.mode")) {
        "NODE" -> {
            launchCloudInstance()
            return
        }

        "MANAGER" -> {
            NodeHandler.start(8181)
            return
        }
    }

    println("Select start mode: NODE, MANAGER")

    val scanner = Scanner(System.`in`)

    while (true) {
        val line = scanner.nextLine()

        if(line == "NODE") {
            launchCloudInstance()
            return
        }

        if(line == "MANAGER") {
            NodeHandler.start(8181)
            return
        }

        println("Select start mode: NODE, MANAGER")
    }
}

fun launchCloudInstance() {
    println("Starting ${CloudInstance::class.java.name}...")

    unixUptime // Just init uptime for correct data.

    System.setProperty("file.encoding", UnixConfiguration.fileEncoding) // Set encoding property from configuration.

    CloudExtensionManager.loadAll(false) // Load all extensions.

    // Use JLine terminal?
    val useJLine = System.getProperty("terminal.jline", "true").toBoolean()

    startKoin {
        allowOverride(true)
        KoinStartEvent(this).callEvent()

        val module = module {
            single<ShutdownHandler>(named("default")) { CloudInstanceShutdownHandler() }
            single<LocationSpace>(named("default")) { CloudLocationSpace }

            if(useJLine) {
                single<Terminal>(named("default")) { JLineTerminal() }
            } else {
                single<Terminal>(named("default")) { DefaultTerminal() }
            }

            single<I18nService>(named("default")) { CloudI18nService }
            single<SaveableLocaleManager>(named("default")) { CloudSaveableLocaleManager() }
            single<CommandDispatcher>(named("default")) { CloudCommandDispatcher }
            single<ServiceManager>(named("default")) { JVMServiceManager }
            single<TemplateManager>(named("default")) { CloudTemplateManager }
            single<GroupWrapperFactoryManager>(named("default")) { CloudGroupWrapperFactoryManager }
            single<GroupManager>(named("default")) { JVMGroupManager }
            single<ModuleManager>(named("default")) { CloudModuleManager }
            single<ExtensionManager>(named("default")) { CloudExtensionManager }
            single<CloudBridge>(named("default")) { JVMBridge }
            single<RemoteService>(named("default")) { CloudRemoteService }
            single<Server>(named("default")) { Server() }
            single<NodeManager>(named("default")) { CloudNodeManager }
        }

        val event = KoinModuleRegisterEvent(mutableListOf(module)).callEvent()

        if(!event.cancelled) modules(event.modules)
    }

    // Start new instance of UnixCloud.
    CloudInstance().start()
}