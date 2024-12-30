package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.wrapper.GroupWrapper
import net.unix.api.group.wrapper.GroupWrapperFactory
import net.unix.api.group.wrapper.GroupWrapperFactoryManager
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [GroupWrapper].
 */
@Suppress("unused")
class GroupWrapperFactoryArgument : CommandArgument<GroupWrapperFactory>(), KoinComponent {

    private val groupWrapperFactoryManager: GroupWrapperFactoryManager by inject(named("default"))

    private var notFoundMessage = "GroupWrapperFactory not found"

    companion object {
        /**
         * Get [GroupWrapperFactory] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [GroupWrapperFactory].
         *
         * @throws IllegalArgumentException If argument not found or is not [GroupWrapperFactory].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getGroupWrapperFactory(name: String): GroupWrapperFactory {
            return this.getArgument(name, GroupWrapperFactory::class.java)
        }
    }

    override fun parse(reader: StringReader): GroupWrapperFactory {
        val factory = groupWrapperFactoryManager[reader.readString()] ?:
            throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return factory
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [GroupWrapperFactoryArgument].
     */
    fun notFound(message: String): GroupWrapperFactoryArgument {
        this.notFoundMessage = message

        return this
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        groupWrapperFactoryManager.factories.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else {
                builder.suggest(it.name)
            }
        }

        return builder.buildFuture()
    }
}