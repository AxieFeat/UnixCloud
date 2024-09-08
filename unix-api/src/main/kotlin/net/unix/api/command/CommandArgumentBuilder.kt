package net.unix.api.command

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider

/**
 * Builder for command arguments.
 */
abstract class CommandArgumentBuilder<T, S> : ArgumentBuilder<T, CommandArgumentBuilder<T, S>>() {

    /**
     * Create argument suggestions
     *
     * @param provider Command suggestions
     *
     * @return Current [CommandArgumentBuilder] instance
     */
    abstract fun suggests(provider: SuggestionProvider<T>?): CommandArgumentBuilder<T, S>


    /**
     * Command executor
     *
     * @param command The result of the command execution. Always return 1
     *
     * @return Current instance of [CommandArgumentBuilder]
     */
    abstract fun execute(command: CommandExecutor<T>?): CommandArgumentBuilder<T, S>
}