package net.unix.api.command.aether.dsl

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.api.command.aether.*
import net.unix.api.command.sender.CommandSender
import net.unix.api.dsl.SimpleDsl
import net.unix.api.dsl.SimpleDsl1

@SimpleDsl1
fun command(name: String, vararg aliases: String, init: CommandDSL.() -> Unit): CommandDSL {
    val command = CommandDSL(name, aliases)
    command.init()
    return command
}

abstract class CommandBase {
    abstract val builder: AetherCommandBuilder

    open fun CommandBase.literal(name: String, init: LiteralDSL.() -> Unit): LiteralDSL {
        val literal = LiteralDSL(builder, name)
        literal.init()
        return literal
    }

    open fun <T> CommandBase.argument(name: String, argument: AetherArgument<T>, init: ArgumentDSL<T>.() -> Unit): ArgumentDSL<T> {
        val arg = ArgumentDSL(builder, name, argument)
        arg.init()
        return arg
    }
}

class CommandDSL(name: String, aliases: Array<out String>) : CommandBase() {
    override val builder = AetherCommandBuilder(name, *aliases)

    @SimpleDsl
    fun executes(command: Command<CommandSender>) {
        builder.executes(command)
    }

    @SimpleDsl
    fun execute(command: AetherCommand<CommandSender>) {
        builder.execute(command)
    }
    fun register(): LiteralCommandNode<CommandSender> {
        return builder.register()
    }
}

class LiteralDSL(
    override val builder: AetherCommandBuilder,
    name: String
) : CommandBase() {
    private val literal = AetherLiteralBuilder.literal(name)

    init {
        builder.then(literal)
    }

    @SimpleDsl
    fun executes(command: Command<CommandSender>) {
        literal.executes(command)
    }

    @SimpleDsl
    fun execute(command: AetherCommand<CommandSender>) {
        literal.execute(command)
    }
}

class ArgumentDSL<T>(
    override val builder: AetherCommandBuilder,
    name: String,
    argumentBuilder: AetherArgument<T>
) : CommandBase() {

    private val argument = AetherArgumentBuilder.argument(name, argumentBuilder)

    init {
        builder.then(argument)
    }

    @SimpleDsl
    fun executes(command: Command<CommandSender>) {
        argument.executes(command)
    }

    @SimpleDsl
    fun execute(command: AetherCommand<CommandSender>) {
        argument.execute(command)
    }

}