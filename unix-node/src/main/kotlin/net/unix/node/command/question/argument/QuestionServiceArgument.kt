package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionServiceArgument : QuestionArgument<CloudService>, KoinComponent {

    private val cloudServiceManager: CloudServiceManager by inject()

    override fun parse(reader: StringReader): CloudService {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            cloudServiceManager[rawName].first().uuid
        }

        val service = cloudServiceManager[uuid]
            ?: throw QuestionParseException("<red>CloudService not found")

        return service
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        cloudServiceManager.services.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}