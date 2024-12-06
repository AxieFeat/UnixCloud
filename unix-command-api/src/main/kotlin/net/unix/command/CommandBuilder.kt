package net.unix.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode

/**
 * Builder for commands.
 *
 * @param name Command name.
 */
@Suppress("LeakingThis", "unused")
abstract class CommandBuilder<T>(
    val name: String
) : LiteralArgumentBuilder<T>(name) {

    /**
     * List of all aliases
     */
    open var aliases = mutableListOf<String>()

    constructor(name: String, vararg aliases: String): this(name) {
        this.aliases = aliases.toMutableList()
    }

    /**
     * Add alias to command.
     *
     * @param name Command alias name.
     *
     * @return Current instance of [CommandBuilder]
     */
    open fun addAlias(vararg name: String): CommandBuilder<T> {
        name.forEach { aliases.add(it) }

        return this
    }

    /**
     * Remove alias from command.
     *
     * @param name Command alias name.
     *
     * @return Current instance of [CommandBuilder].
     */
    open fun removeAlias(vararg name: String): CommandBuilder<T> {
        name.forEach { aliases.remove(it) }

        return this
    }

    /**
     * Register current instance of [CommandBuilder].
     *
     * @return Instance of [LiteralCommandNode].
     */
    abstract fun register(): LiteralCommandNode<T>

    /**
     * Command executor.
     *
     * @param command The result of the command execution. Always return 1.
     *
     * @return Current instance of [CommandBuilder].
     */
    abstract fun execute(command: CommandExecutor<T>?): CommandBuilder<T>

}