package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.Group
import net.unix.api.group.GroupManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionGroupArgument : QuestionArgument<Group>, KoinComponent {

    private val groupManager: GroupManager by inject(named("default"))

    override fun parse(reader: StringReader): Group {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            groupManager[rawName].first().uuid
        }

        val service = groupManager[uuid]
            ?: throw QuestionParseException("<red>CloudGroup not found")

        return service
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        groupManager.groups.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}