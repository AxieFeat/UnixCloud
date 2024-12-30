package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.wrapper.GroupWrapperFactory
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

class QuestionGroupWrapperArgument : QuestionArgument<GroupWrapperFactory>, KoinComponent {

    private val groupWrapperFactoryManager: GroupWrapperFactoryManager by inject(named("default"))

    override fun parse(reader: StringReader): GroupWrapperFactory {
        val executable = groupWrapperFactoryManager[reader.readString()] ?:
            throw QuestionParseException("<red>GroupWrapperFactory not found")

        return executable
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        groupWrapperFactoryManager.factories.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}