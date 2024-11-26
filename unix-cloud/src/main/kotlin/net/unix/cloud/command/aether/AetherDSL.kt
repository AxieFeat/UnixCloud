@file:Suppress("NAME_SHADOWING")

package net.unix.cloud.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.cloud.command.aether.AetherCommandBuilder.Companion.dispatcher
import net.unix.command.CommandExecutor
import net.unix.command.sender.CommandSender
import java.util.function.Predicate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
annotation class CommandDSL2

@DslMarker
annotation class CommandDSL4

@DslMarker
annotation class CommandDSL3

@CommandDSL2
@OptIn(ExperimentalContracts::class)
inline fun command(name: String, vararg aliases: String, setup: AetherDSLCommandBuilder.() -> Unit): AetherDSLCommandBuilder {
    contract {
        callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
    }
    return AetherDSLCommandBuilder(name, *aliases).also(setup)
}

class AetherDSLCommandBuilder(name: String, vararg aliases: String) {

    private val builder = AetherCommandBuilder(name, *aliases)

    fun requires(requirement: Predicate<CommandSender>): AetherDSLCommandBuilder {
        builder.requires(requirement)
        return this
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDSL4
    fun literal(name: String, setup: AetherDSLLiteralBuilder.() -> Unit): AetherDSLLiteralBuilder {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLLiteralBuilder(builder, name).also(setup)

        builder.then(result.literal)

        return result
    }

    @OptIn(ExperimentalContracts::class)
    fun <T> argument(name: String, type: ArgumentType<T>, setup: AetherDSLArgumentBuilder<T>.() -> Unit): AetherDSLArgumentBuilder<T> {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLArgumentBuilder(builder, name, type).also(setup)

        builder.then(result.argument)

        return result
    }

    /**
     * Register current instance of [AetherDSLCommandBuilder]
     *
     * @return Instance of [LiteralCommandNode]
     */
    fun register(): LiteralCommandNode<CommandSender> {
        val command = dispatcher.register(builder)

        builder.aliases.forEach { alias ->
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
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherLiteralBuilder]
     */
    @CommandDSL3
    fun execute(command: CommandExecutor<CommandSender>): AetherDSLCommandBuilder {
        val command = Command { context ->
            command.run(context)
            1
        }

        builder.executes(command)

        return this
    }
}

class AetherDSLLiteralBuilder(
    private val builder: AetherCommandBuilder,
    name: String
) {

    val literal = AetherLiteralBuilder.literal(name)

    fun requires(requirement: Predicate<CommandSender>): AetherDSLLiteralBuilder {
        literal.requires(requirement)
        return this
    }

    @OptIn(ExperimentalContracts::class)
    fun literal(name: String, setup: AetherDSLLiteralBuilder.() -> Unit): AetherDSLLiteralBuilder {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLLiteralBuilder(builder, name).also(setup)

        literal.then(result.literal)

        return result
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDSL4
    fun <T> argument(name: String, type: ArgumentType<T>, setup: AetherDSLArgumentBuilder<T>.() -> Unit): AetherDSLArgumentBuilder<T> {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLArgumentBuilder(builder, name, type).also(setup)

        literal.then(result.argument)

        return result
    }

    /**
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherLiteralBuilder]
     */
    @CommandDSL3
    fun execute(command: CommandExecutor<CommandSender>): AetherDSLLiteralBuilder {
        val command = Command { context ->
            command.run(context)
            1
        }

        literal.executes(command)

        return this
    }
}

class AetherDSLArgumentBuilder<T>(
    private val builder: AetherCommandBuilder,
    name: String,
    type: ArgumentType<T>
) {

    val argument = AetherArgumentBuilder.argument(name, type)

    fun requires(requirement: Predicate<CommandSender>): AetherDSLArgumentBuilder<T> {
        argument.requires(requirement)
        return this
    }

    @OptIn(ExperimentalContracts::class)
    fun literal(name: String, setup: AetherDSLLiteralBuilder.() -> Unit): AetherDSLLiteralBuilder {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLLiteralBuilder(builder, name).also(setup)

        argument.then(result.literal)

        return result
    }

    @OptIn(ExperimentalContracts::class)
    @CommandDSL4
    fun <T> argument(name: String, type: ArgumentType<T>, setup: AetherDSLArgumentBuilder<T>.() -> Unit): AetherDSLArgumentBuilder<T> {
        contract {
            callsInPlace(setup, InvocationKind.EXACTLY_ONCE)
        }

        val result = AetherDSLArgumentBuilder(builder, name, type).also(setup)

        argument.then(result.argument)

        return result
    }

    /**
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherLiteralBuilder]
     */
    @CommandDSL3
    fun execute(command: CommandExecutor<CommandSender>): AetherDSLArgumentBuilder<T> {
        val command = Command { context ->
            command.run(context)
            1
        }

        argument.executes(command)

        return this
    }
}