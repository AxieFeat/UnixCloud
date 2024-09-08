package net.unix.api.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * Used for commands.
 *
 * @see CommandBuilder
 * @see CommandArgumentBuilder
 * @see CommandLiteralBuilder
 */
fun interface CommandExecutor<S> {

    @Throws(CommandSyntaxException::class)
    fun run(context: CommandContext<S>)

}