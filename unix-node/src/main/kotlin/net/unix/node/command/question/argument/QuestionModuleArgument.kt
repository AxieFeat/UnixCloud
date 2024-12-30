package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionModuleArgument : QuestionArgument<Module>, KoinComponent {

    private val moduleManager: ModuleManager by inject(named("default"))

    override fun parse(reader: StringReader): Module {
        val name = reader.readString()

        val extension = moduleManager[name]
            ?: throw QuestionParseException("<red>Module not found")

        return extension
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        moduleManager.modules.forEach {
            builder.suggest(it.info.name)
        }

        return builder.buildFuture()
    }
}