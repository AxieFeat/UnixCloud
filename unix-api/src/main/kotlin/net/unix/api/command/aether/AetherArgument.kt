package net.unix.api.command.aether

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

abstract class AetherArgument<T> : ArgumentType<T> {

    open var exceptionConsumer: Consumer<CommandSyntaxException> = Consumer { ex: CommandSyntaxException -> }

    fun onException(exceptionConsumer: Consumer<CommandSyntaxException>) {
        this.exceptionConsumer = exceptionConsumer
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return Suggestions.empty()
    }
}