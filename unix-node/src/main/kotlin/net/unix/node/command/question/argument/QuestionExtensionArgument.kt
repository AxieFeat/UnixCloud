package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionExtensionArgument : QuestionArgument<Extension>, KoinComponent {

    private val extensionManager: ExtensionManager by inject(named("default"))

    override fun parse(reader: StringReader): Extension {
        val name = reader.readString()

        val extension = extensionManager[name]
            ?: throw QuestionParseException("<red>Extension not found")

        return extension
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        extensionManager.extensions.forEach {
            builder.suggest(it.info.name)
        }

        return builder.buildFuture()
    }
}