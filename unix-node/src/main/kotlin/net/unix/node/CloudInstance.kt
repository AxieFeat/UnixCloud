package net.unix.node

import net.unix.api.LocationSpace
import net.unix.api.ShutdownHandler
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
import net.unix.node.command.CloudCommandDispatcher
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudStartEvent
import net.unix.node.group.GroupJVMWrapper
import net.unix.node.group.rule.CloudRuleHandler
import net.unix.node.logging.CloudLogger
import net.unix.node.modification.extension.CloudExtensionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class CloudInstance : KoinComponent, Startable {

    private val shutdownHandler: ShutdownHandler by inject(named("default"))

    private val terminal: Terminal by inject(named("default"))
    private val locationSpace: LocationSpace by inject(named("default"))

    private val commandDispatcher: CommandDispatcher by inject(named("default"))

    private val i18nService: I18nService by inject(named("default"))
    private val saveableLocaleManager: SaveableLocaleManager by inject(named("default"))

    private val cloudTemplateManager: CloudTemplateManager by inject(named("default"))
    private val cloudGroupManager: CloudGroupManager by inject(named("default"))
    private val cloudServiceManager: CloudServiceManager by inject(named("default"))

    private val moduleManager: ModuleManager by inject(named("default"))
    private val extensionManager: ExtensionManager by inject(named("default"))

    private val server: Server by inject(named("default"))
    private val bridge: CloudBridge by inject(named("default"))
    private val remoteService: RemoteService by inject(named("default"))

    private val nodeManager: NodeManager by inject(named("default"))

    override fun start() {
        terminal.start()


        CloudStartEvent().callEvent()

        server.start(UnixConfiguration.bridge.port)

        Runtime.getRuntime().addShutdownHook(Thread { shutdownHandler.run() })

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

        GroupJVMWrapper.register()
        CloudRuleHandler.start()

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