package net.unix.cloud.command.question.argument.primitive

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.command.question.QuestionArgument
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

class QuestionStringArgument : QuestionArgument<String> {

    override fun parse(reader: StringReader): String {
        return reader.string
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return builder.buildFuture()
    }

}