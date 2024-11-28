package net.unix.command.question

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

interface QuestionArgument<T> {
    fun parse(reader: StringReader): T
    fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions>
}