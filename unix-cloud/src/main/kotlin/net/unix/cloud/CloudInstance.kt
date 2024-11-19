package net.unix.cloud

import com.mojang.brigadier.arguments.IntegerArgumentType
import net.unix.api.CloudAPI
import net.unix.api.CloudBuilder
import net.unix.api.LocationSpace
import net.unix.api.command.CommandDispatcher
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.cloud.CloudInstanceBuilder.Companion.builder
import net.unix.cloud.command.CloudCommandDispatcher
import net.unix.cloud.command.aether.argument.CloudGroupArgument
import net.unix.cloud.command.aether.argument.CloudGroupTypeArgument
import net.unix.cloud.command.aether.command
import net.unix.cloud.configuration.UnixConfiguration
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudStartEvent
import net.unix.cloud.group.BasicCloudGroupManager
import net.unix.cloud.logging.CloudLogger
import net.unix.cloud.modification.extension.CloudExtensionManager
import net.unix.cloud.modification.module.CloudModuleManager
import net.unix.cloud.scheduler.scheduler
import net.unix.cloud.service.BasicCloudServiceManager
import net.unix.cloud.template.BasicCloudTemplateManager
import net.unix.cloud.terminal.CloudJLineTerminal
import kotlin.system.exitProcess

fun main() {
    System.setProperty("file.encoding", UnixConfiguration.fileEncoding)

    var builder: CloudBuilder = CloudInstance.builder()

    scheduler {
        execute {
           CloudExtensionManager.loadAll(false)
        }

        CloudStartEvent(builder).callEvent().also { builder = it.builder }

        builder.build()

        CloudLogger.info("UnixCloud successfully built with ${CloudExtensionManager.extensions.size} extensions")
        CloudExtensionManager.extensions.forEach {
            CloudLogger.info("- ${it.info.name} v${it.info.version}")
        }

        execute {
            CloudInstance.instance.moduleManager.loadAll(false)
        }
    }

    registerCommands()
}

fun registerCommands() {

    command("exit") {
        execute {
            CloudInstance.instance.shutdown()
        }
    }.register()

    /*

    /group
        list - Список групп
        create <название> <лимит сервисов> [тип] - Создать группу
        delete <название> - Удалить группу
        info <название> - Информация о группе

     */
    command("group") {
        literal("list") {
            execute {
                // Список групп
            }
        }

        literal("create") {

            execute {
                // Ошибка
            }

            argument("group", CloudGroupArgument()) {

                execute {
                    // Ошибка
                }

                argument("limit", IntegerArgumentType.integer(0, Int.MAX_VALUE)) {

                    execute {
                        // Создание группы
                    }

                    argument("type", CloudGroupTypeArgument()) {
                        execute {
                            // Создание группы
                        }
                    }
                }
            }
        }

        literal("delete") {

            execute {
                // Ошибка
            }

            argument("group", CloudGroupArgument()) {
                execute {
                    // Удаление группы
                }
            }
        }

        literal("info") {

            execute {
                // Ошибка
            }

            argument("group", CloudGroupArgument()) {
                execute {
                    // Информация о группе
                }
            }
        }
    }
}

@Suppress("unused")
class CloudInstanceBuilder : CloudBuilder {

    private var locationSpace: LocationSpace? = null

    private var commandDispatcher: CommandDispatcher? = null
    private var terminal: Terminal? = null

    private var cloudTemplateManager: CloudTemplateManager? = null
    private var cloudGroupManager: CloudGroupManager? = null
    private var cloudServiceManager: CloudServiceManager? = null

    private var moduleManager: ModuleManager? = null
    private var extensionManager: ExtensionManager? = null

    private var server: Server? = null

    companion object {
        fun CloudInstance.Companion.builder(): CloudInstanceBuilder {
            return CloudInstanceBuilder()
        }
    }

    override fun terminal(terminal: Terminal): CloudInstanceBuilder {
        this.terminal = terminal

        return this
    }

    override fun commandDispatcher(dispatcher: CommandDispatcher): CloudInstanceBuilder {
        this.commandDispatcher = dispatcher

        return this
    }

    override fun commandTemplateManager(manager: CloudTemplateManager): CloudInstanceBuilder {
        this.cloudTemplateManager = manager

        return this
    }

    override fun cloudGroupManager(manager: CloudGroupManager): CloudInstanceBuilder {
        this.cloudGroupManager = manager

        return this
    }

    override fun cloudServiceManager(manager: CloudServiceManager): CloudInstanceBuilder {
        this.cloudServiceManager = manager

        return this
    }

    override fun moduleManager(manager: ModuleManager): CloudInstanceBuilder {
        this.moduleManager = manager

        return this
    }

    override fun extensionManager(manager: ExtensionManager): CloudInstanceBuilder {
        this.extensionManager = manager

        return this
    }

    override fun server(server: Server): CloudInstanceBuilder {
        this.server = server

        return this
    }

    override fun locationSpace(space: LocationSpace): CloudInstanceBuilder {
        this.locationSpace = space
        
        return this
    }

    override fun build(): CloudInstance {
        return CloudInstance(
            locationSpace,
            commandDispatcher,
            terminal,
            cloudTemplateManager,
            cloudGroupManager,
            cloudServiceManager,
            moduleManager,
            extensionManager,
            server
        )
    }
}

class CloudInstance(
    locationSpace: LocationSpace? = null,

    commandDispatcher: CommandDispatcher? = null,
    terminal: Terminal? = null,

    cloudTemplateManager: CloudTemplateManager? = null,
    cloudGroupManager: CloudGroupManager? = null,
    cloudServiceManager: CloudServiceManager? = null,

    moduleManager: ModuleManager? = null,
    extensionManager: ExtensionManager? = null,

    server: Server? = null
) : CloudAPI() {

    companion object {
        private var created = false

        lateinit var instance: CloudAPI
    }

    init {
        if (created) throw Error("CloudInstance already created! Use CloudInstance.instance to get it!")

        instance = this
        created = true
    }

    override val locationSpace: LocationSpace = locationSpace ?: CloudLocationSpace

    override val commandDispatcher: CommandDispatcher = commandDispatcher ?: CloudCommandDispatcher
    override val terminal: Terminal = terminal ?: CloudJLineTerminal(UnixConfiguration.terminal.prompt, this.commandDispatcher)

    override val cloudTemplateManager: CloudTemplateManager = cloudTemplateManager ?: BasicCloudTemplateManager
    override val cloudGroupManager: CloudGroupManager = cloudGroupManager ?: BasicCloudGroupManager
    override val cloudServiceManager: CloudServiceManager = cloudServiceManager ?: BasicCloudServiceManager

    override val moduleManager: ModuleManager = moduleManager ?: CloudModuleManager
    override val extensionManager: ExtensionManager = extensionManager ?: CloudExtensionManager

    override val server: Server = (server ?: Server()).also { it.start(UnixConfiguration.bridge.port) }

    init {
        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler(this).run() })
    }

    override fun shutdown() {
        exitProcess(0)
    }
}