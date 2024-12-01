package net.unix.cloud.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.command.CommandArgument
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.api.template.CloudTemplate
import net.unix.cloud.CloudInstance
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [CloudTemplate].
 */
@Suppress("unused")
class CloudTemplateArgument : CommandArgument<CloudTemplate>() {

    private var notFoundMessage = "CloudTemplate not found"

    companion object {
        /**
         * Get [CloudTemplate] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [CloudTemplate].
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudTemplate].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudTemplate(name: String): CloudTemplate {
            return this.getArgument(name, CloudTemplate::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudTemplate {
        val template = CloudInstance.instance.cloudTemplateManager[reader.readString()]
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
        return CloudInstance.instance.cloudServiceManager.services.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        CloudInstance.instance.cloudTemplateManager.templates.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
                builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}