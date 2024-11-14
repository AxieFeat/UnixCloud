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
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.cloud.CloudInstanceBuilder.Companion.builder
import net.unix.cloud.command.CloudCommandDispatcher
import net.unix.cloud.command.aether.argument.CloudGroupArgument
import net.unix.cloud.command.aether.argument.CloudGroupTypeArgument
import net.unix.cloud.command.aether.command
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudStartEvent
import net.unix.cloud.group.BasicCloudGroupManager
import net.unix.cloud.modification.extension.CloudExtensionManager
import net.unix.cloud.modification.module.CloudModuleManager
import net.unix.cloud.scheduler.CloudSchedulerManager
import net.unix.cloud.service.BasicCloudServiceManager
import net.unix.cloud.template.BasicCloudTemplateManager
import net.unix.cloud.terminal.CloudJLineTerminal
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

fun main() {
    System.setProperty("file.encoding", "UTF-8")

    var builder: CloudBuilder = CloudInstance.builder()

//    //scheduler {
//    //    execute {
//            CloudExtensionManager.loadAll(false)
//    //    }
//
        CloudStartEvent(builder).callEvent().also { builder = it.builder }
//
//        builder.build()
//
//     //   execute {
//            CloudInstance.instance.moduleManager.loadAll(false)
//     //   }
// //   }

    val logger = LoggerFactory.getLogger("main")

    logger.info("123")

//    AetherCommandBuilder("screen") // <- название команды
//        .then( // <- указываем какой-то аргумент
//            literal("toggle") // <- обязательный аргумент "toggle".   Полная запись: AetherLiteralBuilder.literal("toggle")
//                .then( // <- ещё один аргумент, который следует за "toggle"
//                    argument("service", CloudServiceArgument()) // <- CloudServiceArgument фильтрует ввод пользователя и 100% возвращает существующий объект CloudService
//                        .execute { // <- Вызывается при выполнении команды с аргументом toggle и service
//                            val service: CloudService = it["service"] // <- получаем аргумент CloudService по его имени из контекста команды
//
//                            println("Screen toggled to ${service.name}")
//                        }
//                )
//        )
//        .then( // <- указываем ещё какой-то аргумент
//            literal("switch") // <- Обязательный аргумент "switch"
//                .then(
//                    argument("service", CloudServiceArgument())
//                        .execute {
//                            val service: CloudService = it["service"]
//
//                            println("Screen switched to ${service.name}")
//                        }
//                )
//        )
//        .register() // <- регистрируем команду

//    command("screen") { // <- название команды
//        literal("toggle") { // <- указываем какой-то статичный аргумент
//            argument("service", CloudServiceArgument()) { // <- аргумент, требующий ввода пользователя
//                execute { // <- при выполнении команды
//                    val service: CloudService = it["service"] // <- из контекста команды получаем аргумент
//
//                    println("Screen toggled to ${service.name}")
//                }
//            }
//        }
//        literal("switch") {
//            argument("service", CloudServiceArgument()) {
//                execute {
//                    val service: CloudService = it["service"]
//
//                    println("Screen switched to ${service.name}")
//                }
//            }
//        }
//    }.register() // <- регистрируем команду

//    val server = Server() // <- Создаём объект сервера
//    server.start(7979) // <- Запускаем сервер
//
//    // Создаём слушатель по каналу "fun:example"
//    server.createListener("fun:example") { conn, packet ->
//        if (packet == null) return@createListener
//
//        Packet.builder() // <- Создаём пакет'
//            .setChannel("fun:example") // <- Пакет будет отправлен по каналу "fun:example"
//            .asResponseFor(packet) // <- Помечаем пакет как ответ
//            .addNamedBoolean("result" to true) // <- Добавляем данные
//            .send(conn.id, server) // <- Отправляем пакет на клиент
//    }
//
//    val client = Client() // <- Создаём объект клиента
//    client.connect("localhost", 7979) // <- Подключаемся к серверу
//
//    Packet.builder()
//        .setChannel("fun:example")
//        .onResponse { conn, packet -> // <- Будет выполнено при получении ответа на этот пакет
//            // Выполняем какое-то действия
//        }
//        .onResponseTimeout(10000) {
//            // Если в течение 10 секунд пакет не получит ответа - выполнится этот блок кода
//        }
//        .send(client) // <- Отправляем пакет на сервер


//    val server = Server()
//    server.start(7777)
//    println("Сервер запущен!")
//
//    server.createListener("fun:auth") { conn, packet ->
//        if (packet == null) return@createListener
//
//        val name: String? = packet["name"]
//
//        println("Подключен клиент: $name")
//    }
//
//    server.createListener("fun:counter") { conn, packet ->
//        if (packet == null) return@createListener
//
//        val count: Long? = packet["count"]
//
//        println("Число: $count")
//    }
//
//
//    val client = Client()
//    client.connect("localhost", 7777)
//
//    Packet.builder()
//        .setChannel("fun:auth")
//        .addNamedString("name" to "Proxy-1")
//        .send(client)
//
//    scheduler {
//        var counter = 0L
//
//        execute(0, 1000) {
//            Packet.builder()
//                .setChannel("fun:counter")
//                .addNamedLong("count" to counter)
//                .send(client)
//
//            counter++
//        }
//    }

}

fun registerCommands() {
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

    private var schedulerManager: SchedulerManager? = null

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

    override fun schedulerManager(manager: SchedulerManager): CloudInstanceBuilder {
        this.schedulerManager = manager

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
            schedulerManager,
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
    schedulerManager: SchedulerManager? = null,

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

    override val schedulerManager: SchedulerManager = schedulerManager ?: CloudSchedulerManager()

    override val locationSpace: LocationSpace = locationSpace ?: CloudLocationSpace

    override val commandDispatcher: CommandDispatcher = commandDispatcher ?: CloudCommandDispatcher
    override val terminal: Terminal = terminal ?: CloudJLineTerminal(" <white>Unix<gray>@<aqua>cloud<gray>:~<dark_gray># ", this.commandDispatcher)

    override val cloudTemplateManager: CloudTemplateManager = cloudTemplateManager ?: BasicCloudTemplateManager
    override val cloudGroupManager: CloudGroupManager = cloudGroupManager ?: BasicCloudGroupManager
    override val cloudServiceManager: CloudServiceManager = cloudServiceManager ?: BasicCloudServiceManager

    override val moduleManager: ModuleManager = moduleManager ?: CloudModuleManager
    override val extensionManager: ExtensionManager = extensionManager ?: CloudExtensionManager

    override val server: Server = (server ?: Server()).also { it.start(9191) }

    init {
        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler(this).run() })
    }

    override fun shutdown() {
        exitProcess(0)
    }
}