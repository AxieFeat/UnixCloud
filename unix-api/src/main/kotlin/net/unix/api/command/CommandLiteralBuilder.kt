package net.unix.api.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.unix.api.pattern.Nameable

@Suppress("MemberVisibilityCanBePrivate")
abstract class CommandLiteralBuilder<T>(
    override val name: String
) : LiteralArgumentBuilder<T>(name), Nameable {

    /**
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [CommandLiteralBuilder]
     */
    abstract fun execute(command: Command<T>): CommandLiteralBuilder<T>

}