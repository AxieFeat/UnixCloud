package net.unix.cloud.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.GroupExecutable
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

class QuestionGroupExecutableArgument : QuestionArgument<GroupExecutable> {
    override fun parse(reader: StringReader): GroupExecutable {
        val executable = GroupExecutable[reader.readString()] ?:
            throw QuestionParseException("<red>GroupExecutable not found")

        return executable
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        GroupExecutable.executables.forEach {
            builder.suggest(it.key)
        }

        return builder.buildFuture()
    }
}