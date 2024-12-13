package net.unix.launcher.node

import net.unix.api.LocationSpace
import net.unix.api.bridge.CloudBridge
import net.unix.api.group.CloudGroupManager
import net.unix.api.group.SaveableCloudGroupManager
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.node.NodeManager
import net.unix.api.pattern.Startable
import net.unix.api.persistence.PersistentDataType
import net.unix.api.remote.RemoteService
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.template.SaveableCloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.command.CommandDispatcher
import net.unix.node.CloudLocationSpace
import net.unix.node.bridge.JVMBridge
import net.unix.node.command.CloudCommandDispatcher
import net.unix.node.command.question.argument.primitive.QuestionStringArgument
import net.unix.node.command.question.question
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.database.Database
import net.unix.node.database.DatabaseConfiguration
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudStartEvent
import net.unix.node.event.koin.KoinStartEvent
import net.unix.node.group.CloudJVMGroupManager
import net.unix.node.i18n.CloudI18nService
import net.unix.node.i18n.CloudSaveableLocaleManager
import net.unix.node.logging.CloudLogger
import net.unix.node.modification.extension.CloudExtensionManager
import net.unix.node.modification.module.CloudModuleManager
import net.unix.node.node.CloudNodeManager
import net.unix.node.remote.CloudRemoteService
import net.unix.node.service.CloudJVMServiceManager
import net.unix.node.template.BasicCloudTemplateManager
import net.unix.node.terminal.CloudJLineTerminal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

fun launchCloudInstance() {
    println("Starting ${CloudNodeInstance.javaClass.name}...")

    System.setProperty("file.encoding", UnixConfiguration.fileEncoding)

    CloudExtensionManager.loadAll(false)

    startKoin {
        KoinStartEvent(this).callEvent()

        val module = module {
            single<LocationSpace> { CloudLocationSpace }
            single<Terminal> { CloudJLineTerminal() }
            single<I18nService> { CloudI18nService }
            single<SaveableLocaleManager> { CloudSaveableLocaleManager() }
            single<CommandDispatcher> { CloudCommandDispatcher }
            single<CloudServiceManager> { CloudJVMServiceManager }
            single<CloudTemplateManager> { BasicCloudTemplateManager }
            single<CloudGroupManager> { CloudJVMGroupManager }
            single<ModuleManager> { CloudModuleManager }
            single<ExtensionManager> { CloudExtensionManager }
            single<CloudBridge> { JVMBridge }
            single<RemoteService> { CloudRemoteService }
            single<Server> { Server() }
            single<NodeManager> { CloudNodeManager }
        }

        modules(module)
    }

    CloudNodeInstance.start()
}

object CloudNodeInstance : KoinComponent, Startable {

    private val terminal: Terminal by inject()
    private val locationSpace: LocationSpace by inject()

    private val commandDispatcher: CommandDispatcher by inject()

    private val i18nService: I18nService by inject()
    private val saveableLocaleManager: SaveableLocaleManager by inject()

    private val cloudTemplateManager: CloudTemplateManager by inject()
    private val cloudGroupManager: CloudGroupManager by inject()
    private val cloudServiceManager: CloudServiceManager by inject()

    private val moduleManager: ModuleManager by inject()
    private val extensionManager: ExtensionManager by inject()

    private val server: Server by inject()
    private val bridge: CloudBridge by inject()
    private val remoteService: RemoteService by inject()

    private val nodeManager: NodeManager by inject()

    override fun start() {
        terminal.start()

        Database.install(DatabaseConfiguration.connectionInfo)

        CloudStartEvent().callEvent()

        server.start(UnixConfiguration.bridge.port)

        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler.run() })

        saveableLocaleManager.loadAll()
        val selectedLocale = i18nService[UnixConfiguration.terminal.language]

        if (selectedLocale == null) {
            CloudLogger.warning("<yellow>Language with name \"${UnixConfiguration.terminal.language}\" not found!")
        } else {
            i18nService.locale = selectedLocale
        }

        CloudLogger.info("UnixCloud successfully built with ${CloudExtensionManager.extensions.size} extensions")
        CloudExtensionManager.extensions.forEach {
            CloudLogger.info("- ${it.info.name} v${it.info.version}")
        }

        moduleManager.loadAll(false)

        nodeManager.configure(nodeManager.client)

        bridge.configure(server)

        configureRMI(remoteService)
        remoteService.start()

        (commandDispatcher as? CloudCommandDispatcher)?.registerCommands()

        (cloudTemplateManager as? SaveableCloudTemplateManager)?.loadAllTemplates()

        (cloudGroupManager as? SaveableCloudGroupManager)?.loadAllGroups()

    }

    private fun configureRMI(remoteService: RemoteService) {
        remoteService.register(moduleManager)
        remoteService.register(extensionManager)

        remoteService.register(locationSpace)
        remoteService.register(cloudTemplateManager)
        remoteService.register(cloudGroupManager)
        remoteService.register(cloudServiceManager)

        remoteService.register(PersistentDataType.LONG, "LONG")
        remoteService.register(PersistentDataType.BYTE_ARRAY, "BYTE_ARRAY")
        remoteService.register(PersistentDataType.BYTE, "BYTE")
        remoteService.register(PersistentDataType.DOUBLE, "DOUBLE")
        remoteService.register(PersistentDataType.STRING, "STRING")
        remoteService.register(PersistentDataType.FLOAT, "FLOAT")
        remoteService.register(PersistentDataType.INTEGER, "INTEGER")
        remoteService.register(PersistentDataType.INTEGER_ARRAY, "INTEGER_ARRAY")
        remoteService.register(PersistentDataType.LONG_ARRAY, "LONG_ARRAY")
        remoteService.register(PersistentDataType.SHORT, "SHORT")
        remoteService.register(PersistentDataType.TAG_CONTAINER, "TAG_CONTAINER")
        remoteService.register(PersistentDataType.TAG_CONTAINER_ARRAY, "TAG_CONTAINER_ARRAY")
    }
}