package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.template.CloudTemplate
import net.unix.api.template.CloudTemplateManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

class QuestionTemplateArgument : QuestionArgument<CloudTemplate>, KoinComponent {

    private val cloudTemplateManager: CloudTemplateManager by inject()

    override fun parse(reader: StringReader): CloudTemplate {
        val template = cloudTemplateManager[reader.readString()]
            ?: throw QuestionParseException("<red>CloudTemplate not found")

        return template
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        cloudTemplateManager.templates.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}