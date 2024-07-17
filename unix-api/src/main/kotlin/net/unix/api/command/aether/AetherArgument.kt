package net.unix.api.command.aether

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

/**
 * Aether representation of [ArgumentType]
 *
 * @param T Object type
 */
abstract class AetherArgument<T> : ArgumentType<T> {

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return Suggestions.empty()
    }
}