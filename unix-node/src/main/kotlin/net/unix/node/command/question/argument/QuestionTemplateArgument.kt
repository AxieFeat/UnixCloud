package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.template.Template
import net.unix.api.template.TemplateManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

class QuestionTemplateArgument : QuestionArgument<Template>, KoinComponent {

    private val templateManager: TemplateManager by inject(named("default"))

    override fun parse(reader: StringReader): Template {
        val template = templateManager[reader.readString()]
            ?: throw QuestionParseException("<red>CloudTemplate not found")

        return template
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        templateManager.templates.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}