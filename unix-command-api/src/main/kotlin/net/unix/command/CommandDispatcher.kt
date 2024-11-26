@file:Suppress("SpellCheckingInspection")

package net.unix.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.command.sender.CommandSender

/**
 * Console command dispatcher.
 */
interface CommandDispatcher {

    /**
     * Dispatch command.
     *
     * @param sender Command sender.
     * @param command Command line. Example: "mycommand hello world".
     *
     * @return 1, if command dispatched success, else 0.
     *
     * @throws CommandSyntaxException If command has syntax error.
     */
    @Throws(CommandSyntaxException::class)
    fun dispatchCommand(sender: CommandSender, command: String): Int

    /**
     * Dispatch command with already parsed result.
     *
     * @param results Parsed result, can be obtained with [parseCommand].
     *
     * @return 1, if command dispatched success, else 0.
     *
     * @throws CommandSyntaxException If command has syntax error.
     */
    @Throws(CommandSyntaxException::class)
    fun dispatchCommand(results: ParseResults<CommandSender>): Int

    /**
     * Parse command.
     *
     * @param sender Command sender.
     * @param command Command line. Example: "mycommand hello world".
     *
     * @return Command parse result.
     */
    fun parseCommand(sender: CommandSender, command: String): ParseResults<CommandSender>

    /**
     * Instance of [CommandDispatcher] from Brigadier.
     *
     * TIP:
     *
     * If you want to create command you can use:
     *
     * [CommandBuilder],
     * [CommandArgumentBuilder],
     * [CommandLiteralBuilder]
     *
     */
    val dispatcher: CommandDispatcher<CommandSender>

}