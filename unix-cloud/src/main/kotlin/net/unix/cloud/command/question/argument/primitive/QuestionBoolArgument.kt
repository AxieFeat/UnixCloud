package net.unix.cloud.command.question.argument.primitive

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

class QuestionBoolArgument : QuestionArgument<Boolean> {
    override fun parse(reader: StringReader): Boolean {
        val result = reader.readString().toBooleanStrictOrNull()
            ?: throw QuestionParseException("<red>Waiting boolean value")

        return result
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest("true")
        builder.suggest("false")

        return builder.buildFuture()
    }
}