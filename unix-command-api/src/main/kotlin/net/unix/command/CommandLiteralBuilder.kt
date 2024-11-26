package net.unix.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder

@Suppress("MemberVisibilityCanBePrivate")
abstract class CommandLiteralBuilder<T>(
    val name: String
) : LiteralArgumentBuilder<T>(name) {

    /**
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [CommandLiteralBuilder]
     */
    abstract fun execute(command: Command<T>): CommandLiteralBuilder<T>

}