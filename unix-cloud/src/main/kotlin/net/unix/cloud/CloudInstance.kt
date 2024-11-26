package net.unix.cloud

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.unix.api.LocationSpace
import net.unix.api.bridge.CloudBridge
import net.unix.command.CommandDispatcher
import net.unix.api.group.*
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.pattern.Startable
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.*
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import net.unix.api.template.SaveableCloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.bridge.JVMBridge
import net.unix.cloud.command.CloudCommandDispatcher
import net.unix.cloud.command.aether.argument.CloudGroupArgument
import net.unix.cloud.command.aether.argument.GroupExecutableArgument
import net.unix.cloud.command.aether.argument.CloudServiceArgument
import net.unix.cloud.command.aether.argument.CloudTemplateArgument
import net.unix.cloud.command.aether.command
import net.unix.cloud.command.aether.get
import net.unix.cloud.configuration.UnixConfiguration
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudStartEvent
import net.unix.cloud.event.koin.KoinStartEvent
import net.unix.cloud.group.CloudJVMGroupManager
import net.unix.cloud.logging.CloudLogger
import net.unix.cloud.modification.extension.CloudExtensionManager
import net.unix.cloud.modification.module.CloudModuleManager
import net.unix.cloud.service.CloudJVMServiceManager
import net.unix.cloud.template.BasicCloudTemplateManager
import net.unix.cloud.terminal.CloudJLineTerminal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import java.text.SimpleDateFormat
import kotlin.math.floor
import kotlin.system.exitProcess

fun main() {
    System.setProperty("file.encoding", UnixConfiguration.fileEncoding)

    CloudExtensionManager.loadAll(false)

    startKoin {
        KoinStartEvent(this).callEvent()

        val module = module {
            single<LocationSpace> { CloudLocationSpace }
            single<Terminal> { CloudJLineTerminal() }
            single<CommandDispatcher> { CloudCommandDispatcher }
            single<CloudServiceManager> { CloudJVMServiceManager }
            single<CloudTemplateManager> { BasicCloudTemplateManager }
            single<CloudGroupManager> { CloudJVMGroupManager }
            single<ModuleManager> { CloudModuleManager }
            single<ExtensionManager> { CloudExtensionManager }
            single<Server> { Server() }
            single<CloudBridge> { JVMBridge }
        }

        modules(module)
    }

    CloudInstance.start()
}

fun registerCommands(
    terminal: Terminal,
    cloudServiceManager: CloudServiceManager,
    cloudGroupManager: CloudGroupManager,
    cloudTemplateManager: CloudTemplateManager
) {

    command("exit") {
        execute {
            CloudInstance.shutdown()
        }
    }.register()

    /*

    /screen
        list - Список всех сервисов, у которых включён просмотр консоли
        switch <service> - Переключить отправку команд на этот сервис.
        toggle <service> - Переключить просмотр консоли сервиса.
        command <service> <command> - Отправить команду на сервис.


     */
    command("screen") {

        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /screen list - List of all services, where are enabled console view")
            CloudLogger.info("  /screen switch <service> - Switch command sending to this service")
            CloudLogger.info("  /screen toggle <service> - Switching the service console view")
            CloudLogger.info("  /screen command <service> <command> - Send command to service")
        }

        literal("list") {
            execute {
                val services = cloudServiceManager.services
                    .filter { it.executable is ConsoleServiceExecutable }
                    .filter { (it.executable as ConsoleServiceExecutable).viewConsole }

                CloudLogger.info("Console view enabled for ${services.size} services")
                services.forEach {
                    CloudLogger.info(" - ${it.name}")
                }
            }
        }

        literal("switch") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]
                    val executable = service.executable

                    if(executable !is ConsoleServiceExecutable) {
                        CloudLogger.info("Service ${service.name} is not support command sending")
                        return@execute
                    }
                    terminal.selectedExecutable = service.executable as ConsoleServiceExecutable

                    CloudLogger.info("You are selected ${service.name} for command sending")
                }
            }
        }

        literal("toggle") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]
                    val executable = service.executable

                    if(executable is ConsoleServiceExecutable) {
                        executable.viewConsole = !executable.viewConsole
                        CloudLogger.info("Toggled viewing of ${service.name} console")
                        return@execute
                    }

                    CloudLogger.severe("Cant toggle view of ${service.name} console")
                }
            }
        }

        literal("command") {
            argument("service", CloudServiceArgument()) {
                argument("command", StringArgumentType.greedyString()) {
                    execute {
                        val service: CloudService = it["service"]
                        val command: String = it["command"]
                        val executable = service.executable

                        if (executable is ConsoleServiceExecutable) {
                            executable.command(command)
                            CloudLogger.info("Command successfully sent to ${service.name}")
                            return@execute
                        }

                        CloudLogger.severe("Cant sent command to ${service.name}")
                    }
                }
            }
        }
    }.register()

    /*

    /template
        list - Список шаблонов
        create <название> - Создать пустой шаблон
        delete <шаблон> - Удалить шаблон
        info <шаблон> - Информация о шаблоне

     */
    command("template") {

        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /template list - List of all templates")
            CloudLogger.info("  /template create <name> - Create empty template")
            CloudLogger.info("  /template delete <template> - Delete template")
            CloudLogger.info("  /template info <template> - Information about template")
        }

        literal("list") {
            execute {
                val templates = cloudTemplateManager.templates

                CloudLogger.info("List of templates(${templates.size}):")
                templates.forEach {
                    CloudLogger.info("- ${it.name}")
                }
            }
        }

        literal("create") {
            argument("name", StringArgumentType.string()) {
                execute {
                    val name: String = it["name"]

                    CloudLogger.info("Created template $name")

                    cloudTemplateManager.newInstance(
                        name,
                        mutableListOf()
                    )
                }
            }
        }

        literal("delete") {
            argument("template", CloudTemplateArgument()) {
                execute {
                    val template: CloudTemplate = it["template"]

                    template.delete()

                    CloudLogger.info("Deleted template ${template.name}")
                }
            }
        }

        literal("info") {
            argument("template", CloudTemplateArgument()) {
                execute {
                    val template: CloudTemplate = it["template"]

                    CloudLogger.info("Info about ${template.name}:")
                    CloudLogger.info(" - Files:")

                    template.files.forEach { file ->
                        CloudLogger.info("    - From: ${file.from}, To: ${file.to}")
                    }
                }
            }
        }
    }.register()

    /*

    /service
        list - Список сервисов
        info <сервис> - Информация о сервисе
        create <группа> [количество] - создать новые сервисы
        start <сервис> [исполнитель] [перезаписать] - запустить уже существующий сервис
        kill <сервис> - остановить сервис
        delete <сервис> - удалить сервис

     */
    command("service") {

        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /service list - List of all services")
            CloudLogger.info("  /service info <service> - Information about service")
            CloudLogger.info("  /service create <group> [count] - Create new service")
            CloudLogger.info("  /service start <service> [executable] [overwrite] - Start existing service")
            CloudLogger.info("  /service stop <service> - Stop existing service")
            CloudLogger.info("  /service delete <service> - Delete existing service")
        }

        literal("list") {
            execute {
                val services = cloudServiceManager.services

                CloudLogger.info("List of services(${services.size}):")
                services.forEach {
                    CloudLogger.info("- ${it.name}, Group: ${it.group.name}, Status: ${it.status}")
                }
            }
        }

        literal("info") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]

                    val format = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

                    fun formatSeconds(time: Long): String {
                        val secondsLeft = time % 3600 % 60
                        val minutes = floor(time.toDouble() % 3600 / 60).toLong()
                        val hours = floor(time.toDouble() / 3600).toLong()

                        val hh = (if ((hours < 10)) "0" else "") + hours
                        val mm = (if ((minutes < 10)) "0" else "") + minutes
                        val ss = (if ((secondsLeft < 10)) "0" else "") + secondsLeft

                        return "$hh:$mm:$ss"
                    }

                    CloudLogger.info("Info about ${service.name}:")
                    CloudLogger.info(" - UUID: ${service.uuid}")
                    CloudLogger.info(" - Status: ${service.status}")
                    CloudLogger.info(" - Group: ${service.group.name}")
                    CloudLogger.info(" - Memory: ${
                        (service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L)
                        / 1000000
                    }/${
                        (service.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG] ?: 0L)
                        / 1000000
                    } MB")
                    CloudLogger.info(" - Create date: ${format.format(service.created)}")
                    if (service.status == CloudServiceStatus.STARTED)
                        CloudLogger.info(" - Uptime: ${formatSeconds(service.uptime)}")
                    if (service is StaticCloudService)
                        CloudLogger.info(" - Static: ${service.static}")
                }
            }
        }

        literal("create") {
            argument("group", CloudGroupArgument()) {

                execute {
                    val group: CloudGroup = it["group"]

                    val service = try {
                        group.create()
                    } catch (e: CloudGroupLimitException) {
                        CloudLogger.info("Can not create service of group ${group.name}! Limit is ${group.serviceLimit}.")
                        return@execute
                    }

                    CloudLogger.info("Created service ${service.name} from group ${group.name}")
                }

                argument("count", IntegerArgumentType.integer(1, Int.MAX_VALUE)) {
                    execute {
                        val group: CloudGroup = it["group"]
                        val count: Int = it["count"]

                        val services = try {
                            group.create(count)
                        } catch (e: CloudGroupLimitException) {
                            CloudLogger.info("Can not create service of group ${group.name}! Limit is ${group.serviceLimit}.")
                            return@execute
                        }

                        CloudLogger.info("Created ${services.size} services from group ${group.name}")
                    }
                }

            }

        }

        literal("start") {
            argument("service", CloudServiceArgument()) {

                execute {
                    val service: CloudService = it["service"]

                    service.executable?.start() ?: CloudLogger.severe("Cant start ${service.name}, executable not found!")
                }

                fun start(service: CloudService, executable: GroupExecutable, overwrite: Boolean = true) {
                    try {
                        service.start(executable.executableFor(service), overwrite)
                    } catch (e: CloudServiceModificationException) {
                        CloudLogger.info("Service ${service.name} deleted!")
                    } catch (e: IllegalArgumentException) {
                        CloudLogger.info("Service ${service.name} already started!")
                    }
                }

                argument("executable", GroupExecutableArgument()) {
                    execute {
                        val service: CloudService = it["service"]
                        val executable: GroupExecutable = it["executable"]

                        start(service, executable)
                    }

                    argument("overwrite", BoolArgumentType.bool()) {
                        execute {
                            val service: CloudService = it["service"]
                            val executable: GroupExecutable = it["executable"]
                            val overwrite: Boolean = it["overwrite"]

                            start(service, executable, overwrite)
                        }
                    }
                }
            }
        }

        literal("kill") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]

                    try {
                        service.kill(false)
                    } catch (e: CloudServiceModificationException) {
                        CloudLogger.info("Service ${service.name} deleted!")
                    } catch (e: IllegalArgumentException) {
                        CloudLogger.info("Service ${service.name} started, stop it before!")
                    }
                }
            }
        }

        literal("delete") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]

                    try {
                        service.delete()
                    } catch (e: CloudServiceModificationException) {
                        CloudLogger.info("Service ${service.name} already deleted!")
                    } catch (e: IllegalArgumentException) {
                        CloudLogger.info("Stop ${service.name} before delete!")
                    }
                }
            }
        }
    }.register()

    /*

    /group
        list - Список групп
        create <название> <лимит сервисов> <исполняемый файл> [тип] - Создать группу
        delete <название> - Удалить группу
        info <название> - Информация о группе

     */
    command("group") {
        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /group list - List of all groups")
            CloudLogger.info("  /group info <group> - Information about group")
            CloudLogger.info("  /group delete <group> - Delete group.")
            CloudLogger.info("  /group create <name> <service limit> <executable file> [type] - Create a group.")
        }

        literal("list") {
            execute {
                val groups = cloudGroupManager.groups

                CloudLogger.info("List of groups(${groups.size}):")
                groups.forEach {
                    CloudLogger.info("- ${it.name}")
                }
            }
        }

        literal("create") {
            argument("name", StringArgumentType.string()) {
                argument("limit", IntegerArgumentType.integer(0, Int.MAX_VALUE)) {
                    argument("executableFile", StringArgumentType.string()) {

                        execute {

                            val name: String = it["name"]

                            if(name.contains(" ")) {
                                CloudLogger.info("Name of group can not contain spaces!")
                                return@execute
                            }

                            val limit: Int = it["limit"]
                            val executableFile: String = it["executableFile"]

                            CloudLogger.info("Created new group with name $name. Setting it in settings.json file")

                            cloudGroupManager.newInstance(
                                uniqueUUID(),
                                name,
                                limit,
                                executableFile
                            )
                        }

                        argument("type", GroupExecutableArgument()) {
                            execute {
                                val name: String = it["name"]

                                if(name.contains(" ")) {
                                    CloudLogger.info("Name of group can not contain spaces!")
                                    return@execute
                                }

                                val limit: Int = it["limit"]
                                val executableFile: String = it["executableFile"]
                                val type: GroupExecutable = it["type"]

                                CloudLogger.info("Created new group with name $name")

                                cloudGroupManager.newInstance(
                                    uniqueUUID(),
                                    name,
                                    limit,
                                    executableFile,
                                    mutableListOf(),
                                    type
                                )
                            }
                        }
                    }
                }
            }
        }

        literal("delete") {
            argument("group", CloudGroupArgument()) {
                execute {
                    val group: CloudGroup = it["group"]

                    if (group is SaveableCloudGroup) group.delete()

                    CloudLogger.info("Group ${group.uuid} deleted!")
                }
            }
        }

        literal("info") {
            argument("group", CloudGroupArgument()) {
                execute {
                    val group: CloudGroup = it["group"]

                    CloudLogger.info("Info about ${group.name}:")
                    CloudLogger.info(" - UUID: ${group.uuid}")
                    CloudLogger.info(" - Group executable: ${group.groupExecutable?.name ?: "NONE"}")
                    CloudLogger.info(" - Services limit: ${group.serviceLimit}")
                    CloudLogger.info(" - Executable file: ${group.executableFile}")
                    CloudLogger.info(" - Templates(${group.templates.size}):")

                    group.templates.forEach {
                        CloudLogger.info("    - ${it.name}")
                    }

                    CloudLogger.info(" - Sum memory usage: ${
                        run {
                            var result = 0L

                            group.services.forEach { service ->
                                result += service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L
                            }

                            return@run result / 1000000
                        }
                    } MB")
                    CloudLogger.info(" - Services(${group.services.size}):")

                    group.services.forEach { service ->
                        CloudLogger.info("    - ${service.name}, ${service.status}${
                            run {
                                
                                if (service.status != CloudServiceStatus.STARTED) {
                                    return@run ""
                                }

                                val memory =
                                    (service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L) / 1000000

                                return@run ", Usage $memory MB"
                            }
                        }")
                    }
                }
            }
        }
    }.register()
}

object CloudInstance : KoinComponent, Startable {

    private val terminal: Terminal by inject()

    private val cloudTemplateManager: CloudTemplateManager by inject()
    private val cloudGroupManager: CloudGroupManager by inject()
    private val cloudServiceManager: CloudServiceManager by inject()

    private val moduleManager: ModuleManager by inject()

    private  val server: Server by inject()
    private val bridge: CloudBridge by inject()

    override fun start() {
        CloudStartEvent().callEvent()

        terminal.start()

        server.start(UnixConfiguration.bridge.port)

        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler.run() })

        CloudLogger.info("UnixCloud successfully built with ${CloudExtensionManager.extensions.size} extensions")
        CloudExtensionManager.extensions.forEach {
            CloudLogger.info("- ${it.info.name} v${it.info.version}")
        }

        moduleManager.loadAll(false)

        bridge.configure(server)

        (cloudTemplateManager as? SaveableCloudTemplateManager)?.loadAllTemplates()

        (cloudGroupManager as? SaveableCloudGroupManager)?.loadAllGroups()

        registerCommands(terminal, cloudServiceManager, cloudGroupManager, cloudTemplateManager)
    }

    fun shutdown() {
        exitProcess(0)
    }
}