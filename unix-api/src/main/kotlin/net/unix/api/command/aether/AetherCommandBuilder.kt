package net.unix.api.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.api.CloudAPI
import net.unix.api.command.sender.CommandSender
import java.util.function.Predicate

/**
 * Класс для создания команд
 *
 * @param name Название команды, без префикса "/"
 *
 */
class AetherCommandBuilder(private val name: String) : LiteralArgumentBuilder<CommandSender>(name) {

    private var aliases = mutableListOf<String>()

    companion object {
        val dispatcher = CloudAPI.instance.commandDispatcher.dispatcher
    }

    constructor(name: String, vararg aliases: String): this(name) {
        this.aliases = aliases.toMutableList()
    }

    fun addAlias(vararg name: String): AetherCommandBuilder {
        name.forEach { aliases.add(it) }

        return this
    }

    fun removeAlias(vararg name: String): AetherCommandBuilder {
        name.forEach { aliases.remove(it) }

        return this
    }

    fun register(): LiteralCommandNode<CommandSender> {
        val command = dispatcher.register(this)

        aliases.forEach { alias ->
            val builder = AetherCommandBuilder(alias)
                .requires(command.requirement)

            command.children.forEach {
                builder.then(it)
            }

            builder.register()
        }

        return command
    }


    override fun then(argument: ArgumentBuilder<CommandSender, *>?): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    override fun then(argument: CommandNode<CommandSender>?): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    override fun executes(command: Command<CommandSender>?): AetherCommandBuilder {
        return super.executes(command) as AetherCommandBuilder
    }

    override fun requires(requirement: Predicate<CommandSender>?): AetherCommandBuilder {
        return super.requires(requirement) as AetherCommandBuilder
    }

    override fun redirect(target: CommandNode<CommandSender>?): AetherCommandBuilder {
        return super.redirect(target) as AetherCommandBuilder
    }

    override fun redirect(
        target: CommandNode<CommandSender>?,
        modifier: SingleRedirectModifier<CommandSender>?
    ): AetherCommandBuilder {
        return super.redirect(target, modifier) as AetherCommandBuilder
    }

    override fun fork(
        target: CommandNode<CommandSender>?,
        modifier: RedirectModifier<CommandSender>?
    ):AetherCommandBuilder {
        return super.fork(target, modifier) as AetherCommandBuilder
    }

    override fun forward(
        target: CommandNode<CommandSender>?,
        modifier: RedirectModifier<CommandSender>?,
        fork: Boolean
    ): AetherCommandBuilder {
        return super.forward(target, modifier, fork) as AetherCommandBuilder
    }
}