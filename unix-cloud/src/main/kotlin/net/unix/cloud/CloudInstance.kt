package net.unix.cloud

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.unix.api.LocationSpace
import net.unix.api.bridge.CloudBridge
import net.unix.api.group.*
import net.unix.api.group.exception.CloudGroupLimitException
import net.unix.api.i18n.I18nService
import net.unix.api.i18n.SaveableLocaleManager
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.pattern.Startable
import net.unix.api.persistence.PersistentDataType
import net.unix.api.remote.RemoteService
import net.unix.api.service.*
import net.unix.api.service.exception.CloudServiceModificationException
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import net.unix.api.template.SaveableCloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.cloud.CloudExtension.or
import net.unix.cloud.CloudExtension.rem
import net.unix.cloud.CloudExtension.uniqueUUID
import net.unix.cloud.bridge.JVMBridge
import net.unix.cloud.command.CloudCommandDispatcher
import net.unix.cloud.command.aether.argument.*
import net.unix.cloud.command.aether.command
import net.unix.cloud.command.aether.get
import net.unix.cloud.command.question.argument.QuestionGroupExecutableArgument
import net.unix.cloud.command.question.argument.QuestionTemplateArgument
import net.unix.cloud.command.question.argument.primitive.QuestionNumberArgument
import net.unix.cloud.command.question.argument.primitive.QuestionStringArgument
import net.unix.cloud.command.question.question
import net.unix.cloud.configuration.UnixConfiguration
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudStartEvent
import net.unix.cloud.event.koin.KoinStartEvent
import net.unix.cloud.group.CloudJVMGroupManager
import net.unix.cloud.i18n.CloudI18nService
import net.unix.cloud.i18n.CloudSaveableLocaleManager
import net.unix.cloud.logging.CloudLogger
import net.unix.cloud.modification.extension.CloudExtensionManager
import net.unix.cloud.modification.module.CloudModuleManager
import net.unix.cloud.remote.CloudRemoteService
import net.unix.cloud.service.CloudJVMServiceManager
import net.unix.cloud.template.BasicCloudTemplateManager
import net.unix.cloud.terminal.CloudJLineTerminal
import net.unix.command.CommandDispatcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import java.io.File
import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
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
            single<I18nService> { CloudI18nService }
            single<SaveableLocaleManager> { CloudSaveableLocaleManager() }
            single<CommandDispatcher> { CloudCommandDispatcher }
            single<CloudServiceManager> { CloudJVMServiceManager }
            single<CloudTemplateManager> { BasicCloudTemplateManager }
            single<CloudGroupManager> { CloudJVMGroupManager }
            single<ModuleManager> { CloudModuleManager }
            single<ExtensionManager> { CloudExtensionManager }
            single<Server> { Server() }
            single<CloudBridge> { JVMBridge }
            single<RemoteService> { CloudRemoteService }
        }

        modules(module)
    }

    CloudInstance.start()
}

fun registerCommands(
    extensionsManager: ExtensionManager,
    moduleManager: ModuleManager,
    commandDispatcher: CommandDispatcher,
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

    /module
        list - Список всех модулей
        info <module> - Информация о модуле
        load <file> - Загрузить модуль
        unload <module> - Выгрузить модуль
        reload <module> - Перезагрузить модуль

     */
    command("module") {
        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /module list - List of all modules")
            CloudLogger.info("  /module info <module> - Information about module")
            CloudLogger.info("  /module load <file> - Load module from file")
            CloudLogger.info("  /module unload <module> - Unload module")
            CloudLogger.info("  /module reload <module> - Reload module")
        }

        literal("list") {
            execute {
                CloudLogger.info("List of modules(${moduleManager.modules.size}):")

                moduleManager.modules.forEach {
                    CloudLogger.info("- ${it.info.name} v${it.info.version}")
                }
            }
        }

        literal("info") {
            argument("module", CloudModuleArgument()) {
                execute {
                    val module: Module = it["module"]
                    val info = module.info

                    CloudLogger.info("Info about ${info.name}:")
                    CloudLogger.info(" - File: ${module.executable.name}")
                    CloudLogger.info(" - Loader: ${module.loader.javaClass.name}")
                    CloudLogger.info(" - Main-class: ${info.main}")
                    CloudLogger.info(" - Version: ${info.version}")
                    CloudLogger.info(" - Authors: ${info.authors}")
                    CloudLogger.info(" - Description: ${info.description}")
                    CloudLogger.info(" - Website: ${info.website}")
                    CloudLogger.info(" - Dependencies: ${info.depends}")
                    CloudLogger.info(" - Soft dependencies: ${info.soft}")
                }
            }
        }

        literal("load") {
            argument("file", CustomStringArgument.string(
                error = "File not found"
            ) {
                moduleManager.folder.listFiles()
                    ?.map { it.name }
                    ?.filter { !moduleManager.modules.map { it.executable.name }.contains(it) }
                    ?: listOf()
            }) {
                execute {
                    val fileName: String = it["file"]

                    val file = File(moduleManager.folder, fileName)

                    val module = moduleManager.load(file)
                    CloudLogger.info("Loaded module ${module.info.name} v${module.info.version}")
                }
            }
        }

        literal("unload") {
            argument("module", CloudModuleArgument()) {
                execute {
                    val module: Module = it["module"]

                    val result = moduleManager.unload(module)

                    CloudLogger.info(
                        result %
                                "Unloaded module ${module.info.name} v${module.info.version}"
                                or
                                "Cant unload module ${module.info.name} v${module.info.version}"
                    )
                }
            }
        }

        literal("reload") {
            argument("module", CloudModuleArgument()) {
                execute {
                    val module: Module = it["module"]

                    val result = moduleManager.reload(module)

                    CloudLogger.info(
                        result %
                                "Reloaded module ${module.info.name} v${module.info.version}"
                                or
                                "Cant reload module ${module.info.name} v${module.info.version}"
                    )
                }
            }
        }
    }.register()

    /*

    /extension
        list - Список всех расширений
        info <extension> - Информация о расширении

     */
    command("extension") {
        execute {
            CloudLogger.info("Usage:")
            CloudLogger.info("  /extension list - List of all extensions")
            CloudLogger.info("  /extension info <extension> - Information about extension")
        }

        literal("list") {
            execute {
                CloudLogger.info("List of extensions(${extensionsManager.extensions.size}):")

                extensionsManager.extensions.forEach {
                    CloudLogger.info("- ${it.info.name} v${it.info.version}")
                }
            }
        }

        literal("info") {
            argument("extension", CloudExtensionArgument()) {
                execute {
                    val extension: Extension = it["extension"]
                    val info = extension.info

                    CloudLogger.info("Info about ${info.name}:")
                    CloudLogger.info(" - File: ${extension.executable.name}")
                    CloudLogger.info(" - Loader: ${extension.loader.javaClass.name}")
                    CloudLogger.info(" - Main-class: ${info.main}")
                    CloudLogger.info(" - Version: ${info.version}")
                    CloudLogger.info(" - Authors: ${info.authors}")
                    CloudLogger.info(" - Description: ${info.description}")
                    CloudLogger.info(" - Website: ${info.website}")
                }
            }
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
                    .filter { it.executable is ConsoleCloudServiceExecutable }
                    .filter { (it.executable as ConsoleCloudServiceExecutable).viewConsole }

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

                    if(executable !is ConsoleCloudServiceExecutable) {
                        CloudLogger.info("Service ${service.name} is not support command sending")
                        return@execute
                    }
                    terminal.selectedExecutable = service.executable as ConsoleCloudServiceExecutable

                    CloudLogger.info("You are selected ${service.name} for command sending")
                }
            }
        }

        literal("toggle") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]
                    val executable = service.executable

                    if(executable is ConsoleCloudServiceExecutable) {
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

                        if (executable is ConsoleCloudServiceExecutable) {
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
        create - Создать группу
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

            execute {
                createGroup(cloudGroupManager)
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

    CloudLogger.info("Registered ${commandDispatcher.dispatcher.root.children.size} commands")
}

private fun createGroup(cloudGroupManager: CloudGroupManager) {
    question(QuestionNumberArgument.int(1, 6)) {

        var name: String? = null
        var limit: Int? = null
        var templates: MutableSet<CloudTemplate>? = null
        var executableFile: String? = null
        var groupExecutable: GroupExecutable? = null

        fun sendMainMessage() {
            CloudLogger.info("You in group create mode.")
            CloudLogger.info("#################################")
            CloudLogger.info("	Current group settings:")
            CloudLogger.info("	 [1] Name: ${name ?: "-"}")
            CloudLogger.info("	 [2] Service limit: ${limit ?: "-"}")
            CloudLogger.info("	 [3] Templates: ${templates?.map { it.name } ?: "-"}")
            CloudLogger.info("	 [4] Executable file: ${executableFile ?: "-"}")
            CloudLogger.info("	 [5] Group executable: ${groupExecutable?.name ?: "-"}")
            CloudLogger.info("	 [6] Save and exit")
            CloudLogger.info("#################################")
        }

        create {
            sendMainMessage()
        }

        answer {
            when(it) {
                1 -> next(QuestionStringArgument()) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Select group name")
                    }
                    answer { answer ->
                        name = answer
                    }
                }.start()
                2 -> next(QuestionNumberArgument.int(0)) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Set service limit")
                    }
                    answer { answer ->
                        limit = answer.toInt()
                    }
                }.start()
                3 -> next(QuestionTemplateArgument()) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Add template")
                    }
                    answer { answer ->
                        templates?.add(answer) ?: run {
                            templates = mutableSetOf(answer)
                        }
                    }
                }.start()
                4 -> next(QuestionStringArgument()) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Set executable file name")
                    }
                    answer { answer ->
                        executableFile = answer
                    }
                }.start()
                5 -> next(QuestionGroupExecutableArgument()) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Set group executable")
                    }
                    answer { answer ->
                        groupExecutable = answer
                    }
                }.start()
                6 -> next(QuestionNumberArgument.int(1, 2)) {
                    create {
                        CloudLogger.info("You in group create mode.")
                        CloudLogger.info("Do you want exit?")
                        CloudLogger.info("  [1] - Yes")
                        CloudLogger.info("  [2] - No")
                        if(name == null || groupExecutable == null) {
                            CloudLogger.warning("<yellow>Attention! You no set one of this required params:")
                            CloudLogger.warning("<yellow> - Name")
                            CloudLogger.warning("<yellow> - Executable file")
                            CloudLogger.warning("<yellow>If you exit - group will not be created!")
                        }
                    }

                    answer {
                        if(answer == 1) {
                            if (name == null || groupExecutable == null) {
                                CloudLogger.info("You are exit from group create mode.")
                                kill()
                                @Suppress("LABEL_NAME_CLASH")
                                return@answer
                            }

                            cloudGroupManager.newInstance(
                                uniqueUUID(),
                                name!!,
                                limit ?: 1,
                                executableFile!!,
                                templates?.toMutableList() ?: mutableListOf(),
                                groupExecutable
                            )

                            CloudLogger.info("You are created group $name!")
                            kill()
                        }
                    }
                }.start()
            }
        }
    }.start()
}

object CloudInstance : KoinComponent, Startable {

    private val locationSpace: LocationSpace by inject()

    private val terminal: Terminal by inject()
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

    override fun start() {
        CloudStartEvent().callEvent()

        terminal.start()

        server.start(UnixConfiguration.bridge.port)

        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler.run() })

        saveableLocaleManager.loadlAll()
        val selectedLocale = i18nService[UnixConfiguration.terminal.language]

        if(selectedLocale == null)  {
            CloudLogger.warning("<yellow>Language with name \"${UnixConfiguration.terminal.language}\" not found!")
        } else {
            i18nService.locale = selectedLocale
        }

        CloudLogger.info("UnixCloud successfully built with ${CloudExtensionManager.extensions.size} extensions")
        CloudExtensionManager.extensions.forEach {
            CloudLogger.info("- ${it.info.name} v${it.info.version}")
        }

        moduleManager.loadAll(false)

        bridge.configure(server)

        configureRMI(remoteService)
        remoteService.start()

        registerCommands(
            extensionManager,
            moduleManager,
            commandDispatcher,
            terminal,
            cloudServiceManager,
            cloudGroupManager,
            cloudTemplateManager
        )

        (cloudTemplateManager as? SaveableCloudTemplateManager)?.loadAllTemplates()

        (cloudGroupManager as? SaveableCloudGroupManager)?.loadAllGroups()
    }

    fun shutdown() {
        exitProcess(0)
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