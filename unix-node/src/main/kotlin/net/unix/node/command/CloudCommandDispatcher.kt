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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /node list - List of all nodes")
                sender.sendMessage("  /node info <node> - Information about node")
            }

            literal("list") {

                execute {
                    val sender = it.source
                    val nodes = nodeManager.nodes

                    sender.sendMessage("List of all nodes(${nodes.size}):")
                    nodes.forEach { node ->
                        sender.sendMessage(" - ${node.name}")
                    }
                }

            }

            literal("info") {

                argument("node", CloudNodeArgument()) {

                    val format = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

                    execute {
                        val sender = it.source

                        sender.sendMessage("Loading info about node...")

                        scheduler {
                            execute {
                                val node: Node = it["node"]

                                val uptime = node.uptime / 1000
                                val startDate = node.startTime
                                val memoryUsage = node.usageMemory / 1000000
                                val maxMemory = node.maxMemory / 1000000

                                sender.sendMessage("Info about ${node.name}:")
                                sender.sendMessage(" - Uptime: ${formatSeconds(uptime)}")
                                sender.sendMessage(" - Start date: ${format.format(startDate)}")
                                sender.sendMessage(" - Memory: $memoryUsage/$maxMemory MB")
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /module list - List of all modules")
                sender.sendMessage("  /module info <module> - Information about module")
                sender.sendMessage("  /module load <file> - Load module from file")
                sender.sendMessage("  /module unload <module> - Unload module")
                sender.sendMessage("  /module reload <module> - Reload module")
            }

            literal("list") {
                execute {
                    val sender = it.source

                    sender.sendMessage("List of modules(${moduleManager.modules.size}):")

                    moduleManager.modules.forEach { module ->
                        sender.sendMessage("- ${module.info.name} v${module.info.version}")
                    }
                }
            }

            literal("info") {
                argument("module", CloudModuleArgument()) {
                    execute {
                        val sender = it.source
                        val module: Module = it["module"]
                        val info = module.info

                        sender.sendMessage("Info about ${info.name}:")
                        sender.sendMessage(" - File: ${module.executable.name}")
                        sender.sendMessage(" - Loader: ${module.loader.javaClass.name}")
                        sender.sendMessage(" - Main-class: ${info.main}")
                        sender.sendMessage(" - Version: ${info.version}")
                        sender.sendMessage(" - Authors: ${info.authors}")
                        sender.sendMessage(" - Description: ${info.description}")
                        sender.sendMessage(" - Website: ${info.website}")
                        sender.sendMessage(" - Dependencies: ${info.depends}")
                        sender.sendMessage(" - Soft dependencies: ${info.soft}")
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
                        val sender = it.source
                        val fileName: String = it["file"]

                        val file = File(moduleManager.folder, fileName)

                        val module = moduleManager.load(file)
                        sender.sendMessage("Loaded module ${module.info.name} v${module.info.version}")
                    }
                }
            }

            literal("unload") {
                argument("module", CloudModuleArgument()) {
                    execute {
                        val sender = it.source
                        val module: Module = it["module"]

                        val result = moduleManager.unload(module)

                        sender.sendMessage(
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
                        val sender = it.source
                        val module: Module = it["module"]

                        val result = moduleManager.reload(module)

                        sender.sendMessage(
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /extension list - List of all extensions")
                sender.sendMessage("  /extension info <extension> - Information about extension")
            }

            literal("list") {
                execute {
                    val sender = it.source

                    sender.sendMessage("List of extensions(${extensionManager.extensions.size}):")

                    extensionManager.extensions.forEach { extension ->
                        sender.sendMessage("- ${extension.info.name} v${extension.info.version}")
                    }
                }
            }

            literal("info") {
                argument("extension", CloudExtensionArgument()) {
                    execute {
                        val sender = it.source
                        val extension: Extension = it["extension"]
                        val info = extension.info

                        sender.sendMessage("Info about ${info.name}:")
                        sender.sendMessage(" - File: ${extension.executable.name}")
                        sender.sendMessage(" - Loader: ${extension.loader.javaClass.name}")
                        sender.sendMessage(" - Main-class: ${info.main}")
                        sender.sendMessage(" - Version: ${info.version}")
                        sender.sendMessage(" - Authors: ${info.authors}")
                        sender.sendMessage(" - Description: ${info.description}")
                        sender.sendMessage(" - Website: ${info.website}")
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /screen list - List of all services, where are enabled console view")
                sender.sendMessage("  /screen switch <service> - Switch command sending to this service")
                sender.sendMessage("  /screen toggle <service> - Switching the service console view")
                sender.sendMessage("  /screen command <service> <command> - Send command to service")
            }

            literal("list") {
                execute { context ->
                    val sender = context.source
                    val services = serviceManager.services
                        .filter { it.wrapper is ConsoleServiceWrapper }
                        .filter { (it.wrapper as ConsoleServiceWrapper).viewConsole }

                    sender.sendMessage("Console view enabled for ${services.size} services")
                    services.forEach {
                        sender.sendMessage(" - ${it.name}")
                    }
                }
            }

            literal("switch") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]
                        val wrapper = service.wrapper

                        if (wrapper !is ConsoleServiceWrapper) {
                            sender.sendMessage("Service ${service.name} is not support command sending")
                            return@execute
                        }
                        terminal.selectedExecutable = service.wrapper as ConsoleServiceWrapper

                        sender.sendMessage("You are selected ${service.name} for command sending")
                    }
                }
            }

            literal("toggle") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]
                        val wrapper = service.wrapper

                        if (wrapper is ConsoleServiceWrapper) {
                            wrapper.viewConsole = !wrapper.viewConsole
                            sender.sendMessage("Toggled viewing of ${service.name} console")
                            return@execute
                        }

                        sender.sendMessage("Cant toggle view of ${service.name} console")
                    }
                }
            }

            literal("command") {
                argument("service", CloudServiceArgument()) {
                    argument("command", StringArgumentType.greedyString()) {
                        execute {
                            val sender = it.source
                            val service: Service = it["service"]
                            val command: String = it["command"]
                            val wrapper = service.wrapper

                            if (wrapper is ConsoleServiceWrapper) {
                                wrapper.command(command)
                                sender.sendMessage("Command successfully sent to ${service.name}")
                                return@execute
                            }

                            sender.sendMessage("Cant sent command to ${service.name}")
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /template list - List of all templates")
                sender.sendMessage("  /template create <name> - Create empty template")
                sender.sendMessage("  /template delete <template> - Delete template")
                sender.sendMessage("  /template info <template> - Information about template")
            }

            literal("list") {
                execute {
                    val sender = it.source
                    val templates = templateManager.templates

                    sender.sendMessage("List of templates(${templates.size}):")
                    templates.forEach { template ->
                        sender.sendMessage("- ${template.name}")
                    }
                }
            }

            literal("create") {
                argument("name", StringArgumentType.string()) {
                    execute {
                        val sender = it.source
                        val name: String = it["name"]

                        templateManager.register(
                            templateManager.factory.create(
                                name,
                                CloudPersistentDataContainer(),
                                mutableListOf(),
                                mutableListOf()
                            )
                        )

                        sender.sendMessage("Created template $name")
                    }
                }
            }

            literal("delete") {
                argument("template", CloudTemplateArgument()) {
                    execute {
                        val sender = it.source
                        val template: Template = it["template"]

                        template.delete()

                        sender.sendMessage("Deleted template ${template.name}")
                    }
                }
            }

            literal("info") {
                argument("template", CloudTemplateArgument()) {
                    execute {
                        val sender = it.source
                        val template: Template = it["template"]

                        sender.sendMessage("Info about ${template.name}:")
                        sender.sendMessage(" - Files:")

                        template.files.forEach { file ->
                            sender.sendMessage("    - From: ${file.from}, To: ${file.to}")
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /service list - List of all services")
                sender.sendMessage("  /service info <service> - Information about service")
                sender.sendMessage("  /service create <group> [count] - Create new service")
                sender.sendMessage("  /service start <service> [wrapper] [overwrite] - Start existing service")
                sender.sendMessage("  /service kill <service> - Kill started service")
                sender.sendMessage("  /service stop <service> - Try stop service")
                sender.sendMessage("  /service delete <service> - Delete existing service")
                sender.sendMessage("  /service save <service> - Save service files to template")
            }

            literal("list") {
                execute {
                    val sender = it.source
                    val services = serviceManager.services

                    sender.sendMessage("List of services(${services.size}):")
                    services.forEach { service ->
                       sender.sendMessage("- ${service.name}, Group: ${service.group.name}, Status: ${service.status}")
                    }
                }
            }

            literal("info") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        val format = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

                        sender.sendMessage("Info about ${service.name}:")
                        sender.sendMessage(" - UUID: ${service.uuid}")
                        sender.sendMessage(" - Status: ${service.status}")
                        sender.sendMessage(" - Group: ${service.group.name}")
                        sender.sendMessage(
                            " - Memory: ${
                                (service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L)
                                        / 1000000
                            }/${
                                (service.persistentDataContainer[JVMBridge.serviceMaxMemory, PersistentDataType.LONG] ?: 0L)
                                        / 1000000
                            } MB"
                        )
                        sender.sendMessage(" - Create date: ${format.format(service.created)}")
                        if (service.status == ServiceStatus.STARTED || service.status == ServiceStatus.STARTING)
                            sender.sendMessage(" - Uptime: ${formatSeconds(service.uptime / 1000)}")
                        if (service is StaticService)
                            sender.sendMessage(" - Static: ${service.static}")
                    }
                }
            }

            literal("create") {
                argument("group", CloudGroupArgument()) {

                    execute {
                        val sender = it.source
                        val group: Group = it["group"]

                        val service = try {
                            group.create()
                        } catch (e: GroupLimitException) {
                            sender.sendMessage("Can not create service of group ${group.name}! Limit is ${group.serviceLimit}.")
                            return@execute
                        }

                        sender.sendMessage("Created service ${service.name} from group ${group.name}")
                    }

                    argument("count", IntegerArgumentType.integer(1, Int.MAX_VALUE)) {
                        execute {
                            val sender = it.source
                            val group: Group = it["group"]
                            val count: Int = it["count"]

                            val services = try {
                                group.create(count)
                            } catch (e: GroupLimitException) {
                                sender.sendMessage("Can not create service of group ${group.name}! Limit is ${group.serviceLimit}.")
                                return@execute
                            }

                            sender.sendMessage("Created ${services.size} services from group ${group.name}")
                        }
                    }

                }

            }

            literal("start") {
                argument("service", CloudServiceArgument()) {

                    fun start(sender: CommandSender, service: Service, executable: GroupWrapper, overwrite: Boolean = true) {
                        try {
                            service.start(executable.wrapperFor(service), overwrite)
                        } catch (e: ServiceModificationException) {
                            sender.sendMessage("Service ${service.name} deleted!")
                        } catch (e: IllegalArgumentException) {
                            sender.sendMessage("Service ${service.name} already started!")
                        }
                    }

                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        val executable = service.group.wrapper

                        start(sender, service, executable, false)
                    }

                    argument("wrapper", GroupWrapperFactoryArgument()) {
                        execute {
                            val sender = it.source
                            val service: Service = it["service"]
                            val executable: GroupWrapper = it["wrapper"]

                            start(sender, service, executable)
                        }

                        argument("overwrite", BoolArgumentType.bool()) {
                            execute {
                                val sender = it.source
                                val service: Service = it["service"]
                                val executable: GroupWrapper = it["trapper"]
                                val overwrite: Boolean = it["overwrite"]

                                start(sender, service, executable, overwrite)
                            }
                        }
                    }
                }
            }

            literal("kill") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        try {
                            service.kill(false)
                        } catch (e: ServiceModificationException) {
                            sender.sendMessage("Service ${service.name} deleted!")
                        } catch (e: IllegalArgumentException) {
                            sender.sendMessage("Service ${service.name} is not started!")
                        }
                    }
                }
            }

            literal("stop") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        try {
                            service.stop(false)
                        } catch (e: ServiceModificationException) {
                            sender.sendMessage("Service ${service.name} deleted!")
                        } catch (e: IllegalArgumentException) {
                            sender.sendMessage("Service ${service.name} is not started!")
                        }
                    }
                }
            }

            literal("delete") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        try {
                            service.delete()
                        } catch (e: ServiceModificationException) {
                            sender.sendMessage("Service ${service.name} already deleted!")
                        } catch (e: IllegalArgumentException) {
                            sender.sendMessage("Stop ${service.name} before delete!")
                        }
                    }
                }
            }

            literal("save") {
                argument("service", CloudServiceArgument()) {
                    execute {
                        val sender = it.source
                        val service: Service = it["service"]

                        sender.sendMessage("Trying to save ${service.name}...")

                        scheduler {
                            execute {
                                val future = service.updateTemplate()
                                future.get()
                                sender.sendMessage("Service ${service.name} templates updated")
                            }
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
                val sender = it.source

                sender.sendMessage("Usage:")
                sender.sendMessage("  /group list - List of all groups")
                sender.sendMessage("  /group info <group> - Information about group")
                sender.sendMessage("  /group delete <group> - Delete group.")
                sender.sendMessage("  /group create <name> <service limit> <executable file> [type] - Create a group.")
            }

            literal("list") {
                execute {
                    val sender = it.source
                    val groups = groupManager.groups

                    sender.sendMessage("List of groups(${groups.size}):")
                    groups.forEach { group ->
                        sender.sendMessage("- ${group.name}")
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
                        val sender = it.source
                        val group: Group = it["group"]

                        if (group is SaveableGroup) group.delete()

                        sender.sendMessage("Group ${group.uuid} deleted!")
                    }
                }
            }

            literal("info") {
                argument("group", CloudGroupArgument()) {
                    execute { context ->
                        val sender = context.source
                        val group: Group = context["group"]

                        sender.sendMessage("Info about ${group.name}:")
                        sender.sendMessage(" - UUID: ${group.uuid}")
                        sender.sendMessage(" - Group wrapper: ${group.wrapper.name}")
                        sender.sendMessage(" - Services limit: ${group.serviceLimit}")
                        sender.sendMessage(" - Templates(${group.templates.size}):")

                        group.templates.forEach {
                            sender.sendMessage("    - ${it.name}")
                        }

                        sender.sendMessage(" - Sum memory usage: ${
                            run {
                                var result = 0L

                                group.services.forEach { service ->
                                    result += service.persistentDataContainer[JVMBridge.serviceUsedMemory, PersistentDataType.LONG] ?: 0L
                                }

                                return@run result / 1000000
                            }
                        } MB")
                        sender.sendMessage(" - Services(${group.services.size}):")

                        group.services.forEach { service ->
                            sender.sendMessage("    - ${service.name}, ${service.status}${
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

                    4 -> next(QuestionGroupWrapperArgument()) {

                        create {
                            CloudLogger.info("You in group create mode.")
                            CloudLogger.info("Select wrapper factory for setting it.")
                        }

                        answer {
                            scheduler {
                                execute {
                                    val wrapper = it.questionBuilder(this@next).get()
                                    groupWrapper = wrapper
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
                                CloudLogger.warning("<yellow> - Wrapper")
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
                                        groupWrapper!!
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