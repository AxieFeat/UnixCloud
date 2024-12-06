package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionGroupArgument : QuestionArgument<CloudGroup>, KoinComponent {

    private val cloudGroupManager: CloudGroupManager by inject()

    override fun parse(reader: StringReader): CloudGroup {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            cloudGroupManager[rawName].first().uuid
        }

        val service = cloudGroupManager[uuid]
            ?: throw QuestionParseException("<red>CloudGroup not found")

        return service
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        cloudGroupManager.groups.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}