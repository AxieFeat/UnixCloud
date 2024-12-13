package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.GroupWrapper
import net.unix.node.group.AbstractCloudGroupWrapper
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import java.util.concurrent.CompletableFuture

class QuestionGroupWrapperArgument : QuestionArgument<GroupWrapper> {
    override fun parse(reader: StringReader): GroupWrapper {
        val executable = AbstractCloudGroupWrapper[reader.readString()] ?:
            throw QuestionParseException("<red>GroupWrapper not found")

        return executable
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        AbstractCloudGroupWrapper.executables.forEach {
            builder.suggest(it.key)
        }

        return builder.buildFuture()
    }
}