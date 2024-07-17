package net.unix.cloud.command

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.command.CommandDispatcher
import net.unix.api.command.sender.CommandSender

object CommandDispatcherImpl : CommandDispatcher {

    override val dispatcher: com.mojang.brigadier.CommandDispatcher<CommandSender> = com.mojang.brigadier.CommandDispatcher<CommandSender>()

    @Throws(CommandSyntaxException::class)
    override fun dispatchCommand(sender: CommandSender, command: String): Int {
        return dispatcher.execute(command, sender)
    }

    override fun dispatchCommand(results: ParseResults<CommandSender>): Int {
        return dispatcher.execute(results)
    }

    override fun parseCommand(sender: CommandSender, command: String): ParseResults<CommandSender> {
        return dispatcher.parse(command, sender)
    }

}