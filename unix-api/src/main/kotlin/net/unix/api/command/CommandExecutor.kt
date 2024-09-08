package net.unix.api.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException

fun interface CommandExecutor<S> {
    @Throws(CommandSyntaxException::class)
    fun run(context: CommandContext<S>)
}