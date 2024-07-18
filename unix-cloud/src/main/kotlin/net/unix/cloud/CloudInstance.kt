package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.chimera.server.Server
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.aether.AetherArgumentBuilder.Companion.argument
import net.unix.api.command.aether.AetherCommandBuilder
import net.unix.api.command.aether.AetherLiteralBuilder.Companion.literal
import net.unix.api.command.aether.argument.CloudServiceArgument
import net.unix.api.command.aether.get
import net.unix.api.event.impl.cloud.CloudStartEvent
import net.unix.api.group.CloudGroupManager
import net.unix.api.module.CloudModuleManager
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.JLineTerminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory
import net.unix.cloud.command.CommandDispatcherImpl
import net.unix.cloud.module.CloudModuleManagerImpl
import net.unix.cloud.service.CloudServiceManagerImpl
import net.unix.cloud.terminal.JLineTerminalImpl
import net.unix.cloud.terminal.logger.LoggerFactoryImpl
import java.io.File
import kotlin.system.exitProcess

val cloudCommandDispatcher: CommandDispatcher
    get() = CloudInstance.commandDispatcher

val cloudModuleManager: CloudModuleManager
    get() = CloudInstance.moduleManager

val cloudTerminal: JLineTerminal
    get() = CloudInstance.terminal

val cloudLogger: Logger
    get() = CloudInstance.logger

val cloudLoggerFactory: LoggerFactory
    get() = CloudInstance.loggerFactory

fun main() {
    val instance = CloudInstance
    instance.started = true

    AetherCommandBuilder("screen") // <- название команды
        .then( // <- указываем какой-то аргумент
            literal("toggle") // <- обязательный аргумент "toggle".   Полная запись: AetherLiteralBuilder.literal("toggle")
                .then( // <- ещё один аргумент, который следует за "toggle"
                    argument("service", CloudServiceArgument()) // <- CloudServiceArgument фильтрует ввод пользователя и 100% возвращает существующий объект
                        .execute {
                            val service: CloudService = it["service"]

                            println("Screen toggled to ${service.name}")
                        }
                )
        )
        .then( // <- указываем ещё какой-то аргумент
            literal("switch")
                .then(
                    argument("service", CloudServiceArgument())
                        .execute {
                            val service: CloudService = it["service"]

                            println("Screen switched to ${service.name}")
                        }
                )
        )
        .register() // <- регистрируем команду
}

object CloudInstance : CloudAPI() {

    var started: Boolean = false
        set(value) {
            field = value

            if (value) {
                CloudStartEvent().callEvent()
            }
        }

    override val loggerFactory: LoggerFactory = LoggerFactoryImpl()
    override val logger: Logger = loggerFactory.logger

    override val commandDispatcher: CommandDispatcher = CommandDispatcherImpl

    override val cloudServiceManager: CloudServiceManager = CloudServiceManagerImpl
    override val cloudTemplateManager: CloudTemplateManager
        get() = TODO("Not yet implemented")
    override val cloudGroupManager: CloudGroupManager
        get() = TODO("Not yet implemented")
    override val moduleManager: CloudModuleManager = CloudModuleManagerImpl

    override val terminal: JLineTerminal = JLineTerminalImpl(" &fUnix&7@&bcloud&7:~&8# &8")

    override val mainDirectory: File
        get() {
            val path = System.getProperty("user.dir") + "/"

            val file = File(path)

            if (!file.exists()) {
                file.mkdirs()
            }

            return file
        }

    override val server: Server = Server()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler.run() })
        server.start(9191)
    }

    override fun shutdown() {
        exitProcess(0)
    }
}