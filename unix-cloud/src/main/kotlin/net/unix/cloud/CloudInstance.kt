package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.aether.AetherArgumentBuilder.Companion.argument
import net.unix.api.command.aether.AetherCommandBuilder
import net.unix.api.command.aether.AetherLiteralBuilder.Companion.literal
import net.unix.api.command.aether.argument.CloudServiceArgument
import net.unix.api.group.CloudGroupManager
import net.unix.api.module.CloudModuleManager
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
    CloudInstance

    AetherCommandBuilder("screen") // <- название команды
        .then( // <- указываем какой-то аргумент
            literal("toggle") // <- обязательный аргумент "toggle".   Полная запись: AetherLiteralBuilder.literal("toggle")
                .executes { // <- если команда выполнена с аргументом "toggle", то выводим в консоль сообщение
                    println("Usage: /screen toggle <service>")
                    1 // <- результат выполнения команды. 1 - успешно. 0 - не успешно
                }
                .then( // <- ещё один аргумент, который следует за "toggle"
                    argument("service", CloudServiceArgument()) // <- CloudServiceArgument фильтрует ввод пользователя и 100% возвращает существующий объект
                        .executes {
                            println("Screen toggled to ${CloudServiceArgument.getCloudService(it, "service")}")
                            1
                        }
                )
        )
        .then( // <- указываем ещё какой-то аргумент
            literal("switch")
                .executes {
                    println("Usage: /screen switch <service>")
                    1
                }
                .then(
                    argument("service", CloudServiceArgument())
                        .executes {
                            println("Screen switched to ${CloudServiceArgument.getCloudService(it, "service")}")
                            1
                        }
                )
        )
        .executes { // <- если команда выполнена без аргументов
            println("Usage: /screen <action>")
            1
        }
        .register() // <- регистрируем команду
}

object CloudInstance : CloudAPI() {
    override val loggerFactory: LoggerFactory = LoggerFactoryImpl()
    override val logger: Logger = loggerFactory.getLogger()

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
}