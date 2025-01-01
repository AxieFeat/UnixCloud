@file:Suppress("LABEL_NAME_CLASH")

package net.unix.node.command

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.unix.api.group.Group
import net.unix.api.group.GroupManager
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.group.SaveableGroup
import net.unix.api.group.exception.GroupLimitException
import net.unix.api.group.wrapper.GroupWrapperFactory
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.api.node.Node
import net.unix.api.node.NodeManager
import net.unix.api.persistence.PersistentDataType
import net.unix.api.service.*
import net.unix.api.service.exception.ServiceModificationException
import net.unix.api.service.wrapper.ConsoleServiceWrapper
import net.unix.api.template.Template
import net.unix.api.template.TemplateManager
import net.unix.api.terminal.Terminal
import net.unix.command.CommandDispatcher
import net.unix.command.question.NextQuestion
import net.unix.command.sender.CommandSender
import net.unix.node.CloudExtension.or
import net.unix.node.CloudExtension.rem
import net.unix.node.CloudExtension.uniqueUUID
import net.unix.node.bridge.JVMBridge
import net.unix.node.command.aether.argument.*
import net.unix.node.command.aether.command
import net.unix.node.command.aether.get
import net.unix.node.command.question.argument.QuestionGroupWrapperArgument
import net.unix.node.command.question.argument.QuestionTemplateArgument
import net.unix.node.command.question.argument.primitive.QuestionNumberArgument
import net.unix.node.command.question.argument.primitive.QuestionStringArgument
import net.unix.node.command.question.question
import net.unix.node.logging.CloudLogger
import net.unix.node.persistence.CloudPersistentDataContainer
import net.unix.scheduler.impl.scheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import java.text.SimpleDateFormat
import kotlin.math.floor
import kotlin.system.exitProcess

object CloudCommandDispatcher : CommandDispatcher, KoinComponent {

    override val dispatcher: com.mojang.brigadier.CommandDispatcher<CommandSender> =
        com.mojang.brigadier.CommandDispatcher<CommandSender>()

    private val extensionManager: ExtensionManager by inject(named("default"))
    private val moduleManager: ModuleManager by inject(named("default"))
    private val terminal: Terminal by inject(named("default"))
    private val serviceManager: ServiceManager by inject(named("default"))
    private val groupManager: GroupManager by inject(named("default"))
    private val templateManager: TemplateManager by inject(named("default"))
    private val nodeManager: NodeManager by inject(named("default"))

    override fun dispatchCommand(sender: CommandSender, command: String): Int {
        return dispatcher.execute(command, sender)
    }

    override fun dispatchCommand(results: ParseResults<CommandSender>): Int {
        return dispatcher.execute(results)
    }

    override fun parseCommand(sender: CommandSender, command: String): ParseResults<CommandSender> {
        return dispatcher.parse(command, sender)
    }

    fun registerCommands() {

        command("exit") {
            execute {
                exitProcess(0)
            }
        }.register()

        command("node") {

            execute {
                CloudLogger.info("Usage:")
                CloudLogger.info("  /node list - List of all nodes")
                CloudLogger.info("  /node info <node> - Information about node")
            }

            literal("list") {

                execute {
                    val nodes = nodeManager.nodes

                    CloudLogger.info("List of all nodes(${nodes.size}):")
                    nodes.forEach {
                        CloudLogger.info(" - ${it.name}")
                    }
                }

            }

            literal("info") {

                argument("node", CloudNodeArgument()) {

                    val format = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

                    execute {
                        CloudLogger.info("Loading info about node...")

                        scheduler {
                            execute {
                                val node: Node = it["node"]

                                val uptime = node.uptime / 1000
                                val startDate = node.startTime
                                val memoryUsage = node.usageMemory / 1000000
                                val maxMemory = node.maxMemory / 1000000

                                CloudLogger.info("Info about ${node.name}:")
                                CloudLogger.info(" - Uptime: ${formatSeconds(uptime)}")
                                CloudLogger.info(" - Start date: ${format.format(startDate)}")
                                CloudLogger.info(" - Memory: $memoryUsage/$maxMemory MB")
                            }
                        }
                    }
                }

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
                        ?.filter { file -> !moduleManager.modules.map { it.executable.name }.contains(file) }
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
                    CloudLogger.info("List of extensions(${extensionManager.extensions.size}):")

                    extensionManager.extensions.forEach {
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
                    val services = serviceManager.services
                        .filter { it.wrapper is ConsoleServiceWrapper }
                        .filter { (it.wrapper as ConsoleServiceWrapper).viewConsole }

                    CloudLogger.info("Console view enabled for ${services.size} services")
                    services.forEach {
                        CloudLogger.info(" - ${it.name}")
                    }
                }
            }

            literal("switch") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val service: Service = it["service"]
                        val executable = service.wrapper

                        if (executable !is ConsoleServiceWrapper) {
                            CloudLogger.info("Service ${service.name} is not support command sending")
                            return@execute
                        }
                        terminal.selectedExecutable = service.wrapper as ConsoleServiceWrapper

                        CloudLogger.info("You are selected ${service.name} for command sending")
                    }
                }
            }

            literal("toggle") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val service: Service = it["service"]
                        val executable = service.wrapper

                        if (executable is ConsoleServiceWrapper) {
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
                            val service: Service = it["service"]
                            val command: String = it["command"]
                            val executable = service.wrapper

                            if (executable is ConsoleServiceWrapper) {
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
                    val templates = templateManager.templates

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

                        templateManager.register(
                            templateManager.factory.create(
                                name,
                                CloudPersistentDataContainer(),
                                mutableListOf(),
                                mutableListOf()
                            )
                        )
                    }
                }
            }

            literal("delete") {
                argument("template", CloudTemplateArgument()) {
                    execute {
                        val template: Template = it["template"]

                        template.delete()

                        CloudLogger.info("Deleted template ${template.name}")
                    }
                }
            }

            literal("info") {
                argument("template", CloudTemplateArgument()) {
                    execute {
                        val template: Template = it["template"]

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
            start <сервис> [wrapper] [перезаписать] - запустить уже существующий сервис
            kill <сервис> - остановить сервис
            delete <сервис> - удалить сервис

         */
        command("service") {

            execute {
                CloudLogger.info("Usage:")
                CloudLogger.info("  /service list - List of all services")
                CloudLogger.info("  /service info <service> - Information about service")
                CloudLogger.info("  /service create <group> [count] - Create new service")
                CloudLogger.info("  /service start <service> [wrapper] [overwrite] - Start existing service")
                CloudLogger.info("  /service stop <service> - Stop existing service")
                CloudLogger.info("  /service delete <service> - Delete existing service")
            }

            literal("list") {
                execute {
                    val services = serviceManager.services

                    CloudLogger.info("List of services(${services.size}):")
                    services.forEach {
                        CloudLogger.info("- ${it.name}, Group: ${it.group.name}, Status: ${it.status}")
                    }
                }
            }

            literal("info") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val service: Service = it["service"]

                        val format = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

                        CloudLogger.info("Info about ${service.name}:")
                        CloudLogger.info(" - UUID: ${service.uuid}")
                        CloudLogger.info(" - Status: ${service.status}")
                        CloudLogger.info(" - Group: ${service.group.name}")
                        CloudLogger.info(
                            " - Memory: ${
                                (service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L)
                                        / 1000000
                            }/${
                                (service.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG] ?: 0L)
                                        / 1000000
                            } MB"
                        )
                        CloudLogger.info(" - Create date: ${format.format(service.created)}")
                        if (service.status == ServiceStatus.STARTED)
                            CloudLogger.info(" - Uptime: ${formatSeconds(service.uptime / 1000)}")
                        if (service is StaticService)
                            CloudLogger.info(" - Static: ${service.static}")
                    }
                }
            }

            literal("create") {
                argument("group", CloudGroupArgument()) {

                    execute {
                        val group: Group = it["group"]

                        val service = try {
                            group.create()
                        } catch (e: GroupLimitException) {
                            CloudLogger.info("Can not create service of group ${group.name}! Limit is ${group.serviceLimit}.")
                            return@execute
                        }

                        CloudLogger.info("Created service ${service.name} from group ${group.name}")
                    }

                    argument("count", IntegerArgumentType.integer(1, Int.MAX_VALUE)) {
                        execute {
                            val group: Group = it["group"]
                            val count: Int = it["count"]

                            val services = try {
                                group.create(count)
                            } catch (e: GroupLimitException) {
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

                    fun start(service: Service, executable: GroupWrapper, overwrite: Boolean = true) {
                        try {
                            service.start(executable.executableFor(service), overwrite)
                        } catch (e: ServiceModificationException) {
                            CloudLogger.info("Service ${service.name} deleted!")
                        } catch (e: IllegalArgumentException) {
                            CloudLogger.info("Service ${service.name} already started!")
                        }
                    }

                    execute {
                        val service: Service = it["service"]
                        val executable = service.group.wrapper ?: run {
                            CloudLogger.severe("Cant start ${service.name}, executable not found!")
                            return@execute
                        }

                        start(service, executable, false)
                    }

                    argument("wrapper", GroupWrapperFactoryArgument()) {
                        execute {
                            val service: Service = it["service"]
                            val executable: GroupWrapper = it["wrapper"]

                            start(service, executable)
                        }

                        argument("overwrite", BoolArgumentType.bool()) {
                            execute {
                                val service: Service = it["service"]
                                val executable: GroupWrapper = it["trapper"]
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
                        val service: Service = it["service"]

                        try {
                            service.kill(false)
                        } catch (e: ServiceModificationException) {
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
                        val service: Service = it["service"]

                        try {
                            service.delete()
                        } catch (e: ServiceModificationException) {
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
                    val groups = groupManager.groups

                    CloudLogger.info("List of groups(${groups.size}):")
                    groups.forEach {
                        CloudLogger.info("- ${it.name}")
                    }
                }
            }

            literal("create") {

                execute {
                    createGroup()
                }
            }

            literal("delete") {
                argument("group", CloudGroupArgument()) {
                    execute {
                        val group: Group = it["group"]

                        if (group is SaveableGroup) group.delete()

                        CloudLogger.info("Group ${group.uuid} deleted!")
                    }
                }
            }

            literal("info") {
                argument("group", CloudGroupArgument()) {
                    execute { context ->
                        val group: Group = context["group"]

                        CloudLogger.info("Info about ${group.name}:")
                        CloudLogger.info(" - UUID: ${group.uuid}")
                        CloudLogger.info(" - Group wrapper: ${group.wrapper?.name ?: "-"}")
                        CloudLogger.info(" - Services limit: ${group.serviceLimit}")
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

                                    if (service.status != ServiceStatus.STARTED) {
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

        CloudLogger.info("Registered ${dispatcher.root.children.size} commands")
    }

    private fun createGroup() {
        question(QuestionNumberArgument.int(1, 6)) {

            var name: String? = null
            var limit: Int? = null
            var templates: MutableSet<Template>? = null
            var groupWrapper: GroupWrapper? = null

            fun sendMainMessage() {
                CloudLogger.info("You in group create mode.")
                CloudLogger.info("#################################")
                CloudLogger.info("	Current group settings:")
                CloudLogger.info("	 [1] Name: <green>${name ?: "<red>-</red>"}")
                CloudLogger.info("	 [2] Service limit: <green>${limit ?: "<red>-</red>"}")
                CloudLogger.info("	 [3] Templates: <green>${templates?.map { it.name } ?: "<red>-</red>"}")
                CloudLogger.info("	 [4] Setting wrapper (${(groupWrapper?.name == null) % "<red>Not configured</red>" or "<green>Configured</green>"})")
                CloudLogger.info("	 [5] Save and exit")
                CloudLogger.info("#################################")
            }

            create {
                sendMainMessage()
            }

            answer {
                when (it) {
                    1 -> next(QuestionStringArgument()) {
                        create {
                            CloudLogger.info("You in group create mode.")
                            CloudLogger.info("Select group name")
                        }
                        answer { answer ->
                            name = answer
                            close()
                            previous.start()
                        }
                    }.start()

                    2 -> next(QuestionNumberArgument.int(0)) {
                        create {
                            CloudLogger.info("You in group create mode.")
                            CloudLogger.info("Set service limit")
                        }
                        answer { answer ->
                            limit = answer.toInt()
                            close()
                            previous.start()
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
                            close()
                            previous.start()
                        }
                    }.start()

                    4 -> next(QuestionNumberArgument.int(1, 4)) {

                        var wrapperFactory: GroupWrapperFactory? = null
                        var executableFile: String? = null
                        var stopCommand: String? = null

                        create {
                            CloudLogger.info("You in group create mode.")
                            CloudLogger.info("Configuring wrapper of group.")
                            CloudLogger.info("#################################")
                            CloudLogger.info("	Current wrapper settings:")
                            CloudLogger.info("	 [1] Wrapper name: <green>${wrapperFactory?.name ?: "<red>-</red>"}")
                            CloudLogger.info("	 [2] Executable file: <green>${executableFile ?: "<red>-</red>"}")
                            CloudLogger.info("	 [3] Stop command: <green>${stopCommand ?: "<red>-</red>"}")
                            CloudLogger.info("	 [4] Save and return")
                            CloudLogger.info("#################################")
                        }
                        answer { answer ->

                            when(answer) {
                                1 -> {
                                    next(QuestionGroupWrapperArgument()) {
                                        create {
                                            CloudLogger.info("You in group create mode.")
                                            CloudLogger.info("Configuring wrapper of group.")
                                            CloudLogger.info("Set wrapper factory")
                                        }

                                        answer { factory ->
                                            wrapperFactory = factory
                                            close()
                                            previous.start()
                                        }
                                    }.start()
                                }
                                2 -> {
                                    next(QuestionStringArgument()) {
                                        create {
                                            CloudLogger.info("You in group create mode.")
                                            CloudLogger.info("Configuring wrapper of group.")
                                            CloudLogger.info("Set executable file path")
                                        }

                                        answer { name ->
                                            executableFile = name
                                            close()
                                            previous.start()
                                        }
                                    }.start()
                                }
                                3 -> {
                                    next(QuestionStringArgument()) {
                                        create {
                                            CloudLogger.info("You in group create mode.")
                                            CloudLogger.info("Configuring wrapper of group.")
                                            CloudLogger.info("Set stop command")
                                        }

                                        answer { command ->
                                            stopCommand = command
                                            close()
                                            previous.start()
                                        }
                                    }.start()
                                }

                                4 -> {
                                    next(QuestionNumberArgument.int(1, 2)) {
                                        create {
                                            CloudLogger.info("You in group create mode.")
                                            CloudLogger.info("Configuring wrapper of group.")
                                            CloudLogger.info("Do you want exit from configuring wrapper of group?")
                                            CloudLogger.info("  [1] - Yes")
                                            CloudLogger.info("  [2] - No")
                                            if (executableFile == null || wrapperFactory == null) {
                                                CloudLogger.warning("<yellow>Attention! You no set one of this required params:")
                                                CloudLogger.warning("<yellow> - Wrapper name")
                                                CloudLogger.warning("<yellow> - Executable file")
                                                CloudLogger.warning("<yellow>If you exit - settings will not be applied!")
                                            }
                                        }

                                        answer { result ->
                                            when(result) {
                                                1 -> {
                                                    if(executableFile == null || wrapperFactory == null) {
                                                        CloudLogger.info("You are exit from configuring wrapper.")
                                                        close()
                                                        (previous as NextQuestion).previous.start()
                                                        return@answer
                                                    }
                                                    groupWrapper = wrapperFactory!!.createBySerialized(
                                                        mapOf(
                                                            "name" to wrapperFactory!!.name,
                                                            "executable-file" to executableFile!!,
                                                            "stop-command" to (stopCommand ?: "stop")
                                                        )
                                                    )
                                                    close()
                                                    (previous as NextQuestion).previous.start()
                                                }
                                                2 -> {
                                                    close()
                                                    previous.start()
                                                }
                                            }
                                        }
                                    }.start()
                                }
                            }
                        }
                    }.start()

                    5 -> next(QuestionNumberArgument.int(1, 2)) {
                        create {
                            CloudLogger.info("You in group create mode.")
                            CloudLogger.info("Do you want exit?")
                            CloudLogger.info("  [1] - Yes")
                            CloudLogger.info("  [2] - No")
                            if (name == null) {
                                CloudLogger.warning("<yellow>Attention! You no set one of this required params:")
                                CloudLogger.warning("<yellow> - Name")
                                CloudLogger.warning("<yellow>If you exit - group will not be created!")
                            }
                        }

                        answer {
                            if (answer == 1) {
                                if (name == null || groupWrapper == null) {
                                    CloudLogger.info("You are exit from group create mode.")
                                    close()

                                    return@answer
                                }

                                groupManager.register(
                                    groupManager.factory.create(
                                        uuid = uniqueUUID(),
                                        name = name!!,
                                        serviceLimit = limit ?: 1,
                                        templates?.toMutableList() ?: mutableListOf(),
                                        groupWrapper
                                    )
                                )

                                CloudLogger.info("You are created group $name!")
                                close()

                                return@answer
                            }

                            close()
                            previous.start()
                        }
                    }.start()
                }
            }
        }.start()
    }

    private fun formatSeconds(time: Long): String {
        val secondsLeft = time % 3600 % 60
        val minutes = floor(time.toDouble() % 3600 / 60).toLong()
        val hours = floor(time.toDouble() / 3600).toLong()

        val hh = (if ((hours < 10)) "0" else "") + hours
        val mm = (if ((minutes < 10)) "0" else "") + minutes
        val ss = (if ((secondsLeft < 10)) "0" else "") + secondsLeft

        return "$hh:$mm:$ss"
    }

}