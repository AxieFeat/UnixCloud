package net.unix.cloud.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.SingleRedirectModifier
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.api.command.CommandBuilder
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.CommandExecutor
import net.unix.api.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.function.Predicate

/**
 * Get argument from command context by name.
 *
 * @param T Object type.
 * @param name Argument name.
 *
 * @return Argument of type [T].
 *
 * @throws IllegalArgumentException If argument not found or is not type of [T].
 */
@Throws(IllegalArgumentException::class)
inline operator fun <reified T> CommandContext<*>.get(name: String): T {
    return this.getArgument(name, T::class.java)
}

@Suppress("NAME_SHADOWING")
class AetherCommandBuilder(
    name: String
) : CommandBuilder<CommandSender>(name) {

    constructor(name: String, vararg aliases: String): this(name) {
        this.aliases = aliases.toMutableList()
    }

    companion object : KoinComponent {
        private val unixDispatcher: CommandDispatcher by inject()

        val dispatcher = unixDispatcher.dispatcher
    }

    override fun register(): LiteralCommandNode<CommandSender> {
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

    override fun execute(command: CommandExecutor<CommandSender>?): CommandBuilder<CommandSender> {
        val command = Command { context ->
            command?.run(context)
            1
        }
        return super.executes(command) as AetherCommandBuilder
    }


    /**
     * Next command argument.
     *
     * @param argument Instance of [ArgumentBuilder].
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun then(argument: ArgumentBuilder<CommandSender, *>): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    /**
     * Next command argument.
     *
     * @param argument Instance of [CommandNode].
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun then(argument: CommandNode<CommandSender>): AetherCommandBuilder {
        return super.then(argument) as AetherCommandBuilder
    }

    /**
     * Command executor.
     *
     * @param command The result of the command execution.
     * Return 1 if the command was executed successfully, else 0.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun executes(command: Command<CommandSender>?): AetherCommandBuilder {
        return super.executes(command) as AetherCommandBuilder
    }

    /**
     * Command view and execute requirement.
     *
     * @param requirement Command view and execute requirement.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun requires(requirement: Predicate<CommandSender>): AetherCommandBuilder {
        return super.requires(requirement) as AetherCommandBuilder
    }

    /**
     * Redirect that command to other command.
     *
     * @param target Redirect target.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun redirect(target: CommandNode<CommandSender>): AetherCommandBuilder {
        return super.redirect(target) as AetherCommandBuilder
    }

    /**
     * Redirect that command to other command with modifier.
     *
     * @param target Redirect target.
     * @param modifier Command redirect modifier.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun redirect(
        target: CommandNode<CommandSender>,
        modifier: SingleRedirectModifier<CommandSender>
    ): AetherCommandBuilder {
        return super.redirect(target, modifier) as AetherCommandBuilder
    }

    /**
     * Fork that command from other command.
     *
     * @param target Fork target.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun fork(
        target: CommandNode<CommandSender>,
        modifier: RedirectModifier<CommandSender>
    ): AetherCommandBuilder {
        return super.fork(target, modifier) as AetherCommandBuilder
    }

    /**
     * Fork that command from other command with modifier.
     *
     * @param target Fork target.
     * @param modifier Command redirect modifier.
     * @param fork Is fork.
     *
     * @return Current instance of [AetherCommandBuilder].
     */
    override fun forward(
        target: CommandNode<CommandSender>,
        modifier: RedirectModifier<CommandSender>,
        fork: Boolean
    ): AetherCommandBuilder {
        return super.forward(target, modifier, fork) as AetherCommandBuilder
    }
}