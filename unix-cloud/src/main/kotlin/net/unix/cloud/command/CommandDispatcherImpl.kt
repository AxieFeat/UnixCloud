package net.unix.cloud.command

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.aether.AetherCommandBuilder
import net.unix.api.command.aether.AetherArgumentBuilder
import net.unix.api.command.sender.CommandSender

object CommandDispatcherImpl : CommandDispatcher {

    override val dispatcher: com.mojang.brigadier.CommandDispatcher<CommandSender> = com.mojang.brigadier.CommandDispatcher<CommandSender>()

    init {
        dispatcher.register(
            LiteralArgumentBuilder.literal<CommandSender>("test")
                .then(
                    RequiredArgumentBuilder.argument<CommandSender, String>("arg", StringArgumentType.string())
                        .suggests { _, suggestionsBuilder ->
                            suggestionsBuilder.suggest("test")
                            suggestionsBuilder.suggest("test2")
                            suggestionsBuilder.suggest("test5")

                            return@suggests suggestionsBuilder.buildFuture()
                        }
                        .executes {
                            println("Вы написали: ${StringArgumentType.getString(it, "arg")}")
                            1
                        }
                )
                .executes {
                    println("Использование: /test <строка>")
                    1
                }
        )

    }

    @Throws(CommandSyntaxException::class)
    override fun dispatchCommand(sender: CommandSender, command: String): Int {
        return dispatcher.execute(command, sender)
    }

    override fun dispatchCommand(results: ParseResults<CommandSender>): Int {
        return dispatcher.execute(results)
    }

    override fun parseCommand(sender: CommandSender, command: String): ParseResults<CommandSender> {
        return dispatcher.parse(command, sender)
    }

}