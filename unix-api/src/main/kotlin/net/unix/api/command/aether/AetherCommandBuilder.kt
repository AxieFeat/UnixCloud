package net.unix.api.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.api.CloudAPI
import net.unix.api.command.sender.CommandSender
import java.util.function.Predicate

/**
 * Get argument from command context by name
 *
 * @param T Object type
 * @param name Argument name
 *
 * @return Argument of type [T]
 *
 * @throws IllegalArgumentException If argument not found or is not type of [T]
 */
@Throws(IllegalArgumentException::class)
inline operator fun <reified T> CommandContext<*>.get(name: String): T {
    return this.getArgument(name, T::class.java)
}

/**
 * Command builder class
 *
 * @param name Command name, without "/" prefix!
 */
class AetherCommandBuilder(private val name: String) : LiteralArgumentBuilder<CommandSender>(name) {

    private var aliases = mutableListOf<String>()

    companion object {
        val dispatcher = CloudAPI.instance.commandDispatcher.dispatcher
    }

    /**
     * One of constructors [AetherCommandBuilder]
     *
     * @param name Command name, without "/" prefix!
     * @param aliases Command aliases
     */
    constructor(name: String, vararg aliases: String): this(name) {
        this.aliases = aliases.toMutableList()
    }

    /**
     * Add alias to command
     *
     * @param name Command alias name
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    fun addAlias(vararg name: String): AetherCommandBuilder {
        name.forEach { aliases.add(it) }

        return this
    }

    /**
     * Remove alias from command
     *
     * @param name Command alias name
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    fun removeAlias(vararg name: String): AetherCommandBuilder {
        name.forEach { aliases.remove(it) }

        return this
    }

    /**
     * Register current instance of [AetherCommandBuilder]
     *
     * @return Instance of [LiteralCommandNode]
     */
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

    /**
     * Next command argument
     *
     * @param argument Instance of [ArgumentBuilder]
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun then(argument: ArgumentBuilder<CommandSender, *>?): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    /**
     * Next command argument
     *
     * @param argument Instance of [CommandNode]
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun then(argument: CommandNode<CommandSender>?): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    /**
     * Command executor
     *
     * @param command The result of the command execution. Return 1 if the command was executed successfully, else 0
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun executes(command: Command<CommandSender>?): AetherCommandBuilder {
        return super.executes(command) as AetherCommandBuilder
    }

    /**
     * Command executor
     *
     * @param aetherCommand The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    fun execute(aetherCommand: AetherCommand<CommandSender>?): AetherCommandBuilder {
        val command = Command { context ->
            aetherCommand?.run(context)
            1
        }
        return super.executes(command) as AetherCommandBuilder
    }

    /**
     * Command view and execute requirement
     *
     * @param requirement Command view and execute requirement
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun requires(requirement: Predicate<CommandSender>?): AetherCommandBuilder {
        return super.requires(requirement) as AetherCommandBuilder
    }

    /**
     * Redirect that command to other command
     *
     * @param target Redirect target
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun redirect(target: CommandNode<CommandSender>?): AetherCommandBuilder {
        return super.redirect(target) as AetherCommandBuilder
    }

    /**
     * Redirect that command to other command with modifier
     *
     * @param target Redirect target
     * @param modifier Command redirect modifier
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun redirect(
        target: CommandNode<CommandSender>?,
        modifier: SingleRedirectModifier<CommandSender>?
    ): AetherCommandBuilder {
        return super.redirect(target, modifier) as AetherCommandBuilder
    }

    /**
     * Fork that command from other command
     *
     * @param target Fork target
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun fork(
        target: CommandNode<CommandSender>?,
        modifier: RedirectModifier<CommandSender>?
    ):AetherCommandBuilder {
        return super.fork(target, modifier) as AetherCommandBuilder
    }

    /**
     * Fork that command from other command with modifier
     *
     * @param target Fork target
     * @param modifier Command redirect modifier
     * @param fork Is fork
     *
     * @return Current instance of [AetherCommandBuilder]
     */
    override fun forward(
        target: CommandNode<CommandSender>?,
        modifier: RedirectModifier<CommandSender>?,
        fork: Boolean
    ): AetherCommandBuilder {
        return super.forward(target, modifier, fork) as AetherCommandBuilder
    }
}

fun interface AetherCommand<S> {
    @Throws(CommandSyntaxException::class)
    fun run(context: CommandContext<S>)
}