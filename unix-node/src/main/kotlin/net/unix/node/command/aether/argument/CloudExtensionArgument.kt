package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.modification.extension.Extension
import net.unix.api.modification.extension.ExtensionManager
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Extension].
 */
class CloudExtensionArgument  : CommandArgument<Extension>(), KoinComponent {

    private val extensionsManager: ExtensionManager by inject()
    private var notFoundMessage = "Extensions not found"

    companion object {

        /**
         * Get [Extension] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Extension].
         *
         * @throws IllegalArgumentException If argument not found or is not [Extension].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getExtensions(name: String): Extension {
            return this.getArgument(name, Extension::class.java)
        }
    }

    override fun parse(reader: StringReader): Extension {
        val name = reader.readString()

        val extension = extensionsManager[name] ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return extension
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudExtensionArgument].
     */
    fun notFound(message: String): CloudExtensionArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return extensionsManager.extensions.map { it.info.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        extensionsManager.extensions.forEach {
            if (it.info.name.contains(" ")) {
                builder.suggest("\"${it.info.name}\"")
            } else
                builder.suggest(it.info.name)
        }

        return builder.buildFuture()
    }

}