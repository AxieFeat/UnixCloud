package net.unix.api.command.aether.dsl

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import net.unix.api.command.aether.AetherArgumentBuilder
import net.unix.api.command.aether.AetherCommandBuilder
import net.unix.api.command.aether.AetherLiteralBuilder
import net.unix.api.command.sender.CommandSender

fun command(param: AetherDSL.() -> Unit): AetherCommandBuilder {
    val dsl = AetherDSL()
    param.invoke(dsl)
    return dsl.builder
}

fun command(name: String, vararg aliases: String, param: AetherDSL.() -> Unit): AetherCommandBuilder {
    val dsl = AetherDSL(name, aliases)
    param.invoke(dsl)
    return dsl.builder
}

class AetherDSL() {

    companion object {

    }

    lateinit var builder: AetherCommandBuilder

    var name: String = ""
        set(value) {
            field = value

            builder = AetherCommandBuilder(name)
        }

    var aliases = listOf<String>()
        set(value) {
            field = value

            builder.addAlias(*value.toTypedArray())
        }

    constructor(name: String, aliases: Array<out String>): this() {
        this.name = name
        this.aliases = aliases.toList()
    }

    private var last: ArgumentBuilder<Any, *>? = null

    fun executes(command: Command<CommandSender>) {
        if (last == null) {
            builder.executes(command)
            return
        }

        last!!.executes(command as Command<Any>)
        last = null
    }

    fun literal(literal: LiteralArgument.() -> Unit) {
        val command = LiteralArgument()
        literal.invoke(command)

        val aether = AetherLiteralBuilder(command.name)

        if (last != null) {
            last!!.then(aether as ArgumentBuilder<Any, *> )
        } else {
            builder.then(aether)
        }

        last = aether as ArgumentBuilder<Any, *>
    }

    fun literal(name: String, literal: LiteralArgument.() -> Unit) {
        val command = LiteralArgument(name)
        literal.invoke(command)

        val aether = AetherLiteralBuilder(command.name)

        if (last != null) {
            last!!.then(aether as ArgumentBuilder<Any, *> )
        } else {
            builder.then(aether)
        }

        last = aether as ArgumentBuilder<Any, *>
    }

    fun argument(argument: RequiredArgument.() -> Unit) {
        val requiredArgument = RequiredArgument()
        argument.invoke(requiredArgument)

        val aether = AetherArgumentBuilder(requiredArgument.name, requiredArgument.type)

        if (last != null) {
            last!!.then(aether as ArgumentBuilder<Any, *>)
        } else {
            builder.then(aether)
        }

        last = aether as ArgumentBuilder<Any, *>

    }

    fun <T> argument(name: String, type: ArgumentType<T>, argument: RequiredArgument.() -> Unit) {
        val requiredArgument = RequiredArgument(name, type)
        argument.invoke(requiredArgument)

        val aether = AetherArgumentBuilder(requiredArgument.name, requiredArgument.type)

        if (last != null) {
            last!!.then(aether as ArgumentBuilder<Any, *>)
        } else {
            builder.then(aether)
        }

        last = aether as ArgumentBuilder<Any, *>
    }

    class LiteralArgument() {
        lateinit var name: String

        constructor(name: String): this() {
            this.name = name
        }
    }

    class RequiredArgument() {
        lateinit var name: String
        lateinit var type: ArgumentType<*>

        constructor(name: String, type: ArgumentType<*>): this() {
            this.name = name
            this.type = type
        }
    }
}