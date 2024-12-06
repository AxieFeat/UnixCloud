package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Module].
 */
@Suppress("unused")
class CloudModuleArgument : CommandArgument<Module>(), KoinComponent {

    private val moduleManager: ModuleManager by inject()
    private var notFoundMessage = "Module not found"

    companion object {

        /**
         * Get [Module] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Module].
         *
         * @throws IllegalArgumentException If argument not found or is not [Module].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getModule(name: String): Module {
            return this.getArgument(name, Module::class.java)
        }
    }

    override fun parse(reader: StringReader): Module {
        val name = reader.readString()

        val module = moduleManager[name] ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return module
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudModuleArgument].
     */
    fun notFound(message: String): CloudModuleArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return moduleManager.modules.map { it.info.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        moduleManager.modules.forEach {
            if (it.info.name.contains(" ")) {
                builder.suggest("\"${it.info.name}\"")
            } else
                builder.suggest(it.info.name)
        }

        return builder.buildFuture()
    }

}