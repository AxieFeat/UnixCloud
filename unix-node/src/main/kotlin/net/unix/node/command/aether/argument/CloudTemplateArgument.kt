package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.api.template.Template
import net.unix.api.template.TemplateManager
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Template].
 */
@Suppress("unused")
class CloudTemplateArgument : CommandArgument<Template>(), KoinComponent {

    private val templateManager: TemplateManager by inject(named("default"))
    private var notFoundMessage = "CloudTemplate not found"

    companion object {

        /**
         * Get [Template] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Template].
         *
         * @throws IllegalArgumentException If argument not found or is not [Template].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudTemplate(name: String): Template {
            return this.getArgument(name, Template::class.java)
        }
    }

    override fun parse(reader: StringReader): Template {
        val template = templateManager[reader.readString()]
            ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return template
    }

    /**
     * Set your own not found message
     *
     * @param message Message text
     *
     * @return Current instance of [CloudTemplateArgument]
     */
    fun notFound(message: String): CloudTemplateArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return templateManager.templates.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        templateManager.templates.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
                builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}