package net.unix.cloud.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import net.unix.command.CommandArgumentBuilder
import net.unix.command.CommandExecutor
import net.unix.command.sender.CommandSender

@Suppress("NAME_SHADOWING")
class AetherArgumentBuilder<T>(
    val name: String,
    private val type: ArgumentType<T>,
    private var suggestionsProvider: SuggestionProvider<CommandSender>? = null
): CommandArgumentBuilder<CommandSender, AetherArgumentBuilder<T>>() {

    companion object {
        /**
         * Create instance of [AetherArgumentBuilder]
         *
         * @param T Object type
         * @param name Argument name
         * @param type [ArgumentType], which returns [T]
         */
        fun <T> argument(name: String, type: ArgumentType<T>): AetherArgumentBuilder<T> {
            return AetherArgumentBuilder(name, type)
        }
    }


    override fun suggests(provider: SuggestionProvider<CommandSender>?): CommandArgumentBuilder<CommandSender, AetherArgumentBuilder<T>> {
        this.suggestionsProvider = provider

        return getThis()
    }

    override fun execute(command: CommandExecutor<CommandSender>?): CommandArgumentBuilder<CommandSender, AetherArgumentBuilder<T>> {
        val command = Command { context ->
            command?.run(context)
            1
        }
        return super.executes(command) as AetherArgumentBuilder<T>
    }

    override fun getThis(): CommandArgumentBuilder<CommandSender, AetherArgumentBuilder<T>> = this


    override fun build(): CommandNode<CommandSender> {
        val result = ArgumentCommandNode(
            name, type,
            command, requirement, redirect, redirectModifier, isFork, suggestionsProvider
        )

        for (argument in arguments) {
            result.addChild(argument)
        }

        return result
    }

}