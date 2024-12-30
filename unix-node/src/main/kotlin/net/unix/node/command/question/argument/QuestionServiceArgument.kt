package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionServiceArgument : QuestionArgument<Service>, KoinComponent {

    private val serviceManager: ServiceManager by inject(named("default"))

    override fun parse(reader: StringReader): Service {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            serviceManager[rawName].first().uuid
        }

        val service = serviceManager[uuid]
            ?: throw QuestionParseException("<red>CloudService not found")

        return service
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        serviceManager.services.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}