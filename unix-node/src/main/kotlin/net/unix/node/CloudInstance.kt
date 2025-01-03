package net.unix.node

import net.unix.api.LocationSpace
import net.unix.api.ShutdownHandler
import net.unix.api.bridge.CloudBridge
import net.unix.api.group.GroupManager
import net.unix.api.group.SaveableGroupManager
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.node.NodeManager
import net.unix.api.pattern.Startable
import net.unix.api.persistence.PersistentDataType
import net.unix.api.remote.RemoteService
import net.unix.api.service.ServiceManager
import net.unix.api.template.SaveableTemplateManager
import net.unix.api.template.TemplateManager
import net.unix.api.terminal.Terminal
import net.unix.command.CommandDispatcher
import net.unix.node.command.CloudCommandDispatcher
import net.unix.node.configuration.UnixConfiguration
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudStartEvent
import net.unix.node.group.rule.CloudRuleHandler
import net.unix.node.i18n.translatable
import net.unix.node.logging.CloudLogger
import net.unix.node.modification.extension.CloudExtensionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * This class represents the instance of UnixCloud.
 *
 * In general - you can create multiple instances, but don't expect this to work with standard implementations.
 */
class CloudInstance : KoinComponent, Startable {

    private val shutdownHandler: ShutdownHandler by inject(named("default"))

    private val terminal: Terminal by inject(named("default"))
    private val locationSpace: LocationSpace by inject(named("default"))

    private val commandDispatcher: CommandDispatcher by inject(named("default"))

    private val i18nService: I18nService by inject(named("default"))
    private val saveableLocaleManager: SaveableLocaleManager by inject(named("default"))

    private val templateManager: TemplateManager by inject(named("default"))
    private val groupWrapperFactoryManager: GroupWrapperFactoryManager by inject(named("default"))
    private val groupManager: GroupManager by inject(named("default"))
    private val serviceManager: ServiceManager by inject(named("default"))

    private val moduleManager: ModuleManager by inject(named("default"))
    private val extensionManager: ExtensionManager by inject(named("default"))

    private val server: Server by inject(named("default"))
    private val bridge: CloudBridge by inject(named("default"))
    private val remoteService: RemoteService by inject(named("default"))

    private val nodeManager: NodeManager by inject(named("default"))

    /**
     * Just start the instance.
     */
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

        CloudLogger.info(translatable("start.built", CloudExtensionManager.extensions.size))

        CloudExtensionManager.extensions.forEach {
            CloudLogger.info("- ${it.info.name} v${it.info.version}")
        }

        moduleManager.loadAll(false)

        nodeManager.configure(nodeManager.client)

        bridge.configure(server)

        configureRMI(remoteService)
        remoteService.start()

        CloudRuleHandler.start()

        (commandDispatcher as? CloudCommandDispatcher)?.registerCommands()

        (templateManager as? SaveableTemplateManager)?.loadAllTemplates()

        (groupManager as? SaveableGroupManager)?.loadAllGroups()
    }

    /**
     * Configuring RMI server.
     */
    private fun configureRMI(remoteService: RemoteService) {
        remoteService.register(ModuleManager::class, moduleManager)
        remoteService.register(ExtensionManager::class, extensionManager)

        remoteService.register(LocationSpace::class, locationSpace)
        remoteService.register(TemplateManager::class, templateManager)
        remoteService.register(GroupWrapperFactoryManager::class, groupWrapperFactoryManager)
        remoteService.register(GroupManager::class, groupManager)
        remoteService.register(ServiceManager::class, serviceManager)

        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.LONG, "LONG")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.BYTE_ARRAY, "BYTE_ARRAY")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.BYTE, "BYTE")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.DOUBLE, "DOUBLE")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.STRING, "STRING")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.FLOAT, "FLOAT")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.INTEGER, "INTEGER")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.INTEGER_ARRAY, "INTEGER_ARRAY")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.LONG_ARRAY, "LONG_ARRAY")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.SHORT, "SHORT")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.TAG_CONTAINER, "TAG_CONTAINER")
        remoteService.register(PersistentDataType.PrimitivePersistentDataType::class, PersistentDataType.TAG_CONTAINER_ARRAY, "TAG_CONTAINER_ARRAY")
    }
}