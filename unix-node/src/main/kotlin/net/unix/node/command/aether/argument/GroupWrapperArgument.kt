package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.GroupWrapper
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.node.group.AbstractCloudGroupWrapper
import net.unix.command.CommandArgument
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [GroupWrapper].
 */
@Suppress("unused")
class GroupWrapperArgument : CommandArgument<GroupWrapper>() {

    private var notFoundMessage = "GroupWrapper not found"

    companion object {
        /**
         * Get [GroupWrapper] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [GroupWrapper].
         *
         * @throws IllegalArgumentException If argument not found or is not [GroupWrapper].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getGroupExecutable(name: String): GroupWrapper {
            return this.getArgument(name, GroupWrapper::class.java)
        }
    }

    override fun parse(reader: StringReader): GroupWrapper {
        val executable = AbstractCloudGroupWrapper[reader.readString()] ?:
            throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return executable
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [GroupWrapperArgument].
     */
    fun notFound(message: String): GroupWrapperArgument {
        this.notFoundMessage = message

        return this
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        AbstractCloudGroupWrapper.executables.forEach {
            if (it.key.contains(" ")) {
                builder.suggest("\"${it.key}\"")
            } else
                builder.suggest(it.key)
        }

        return builder.buildFuture()
    }
}