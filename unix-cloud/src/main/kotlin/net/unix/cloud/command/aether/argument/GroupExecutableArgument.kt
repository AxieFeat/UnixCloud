package net.unix.cloud.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.group.GroupExecutable
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.command.CommandArgument
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [GroupExecutable].
 */
class GroupExecutableArgument : CommandArgument<GroupExecutable>() {

    private var notFoundMessage = "GroupExecutable not found"

    companion object {
        /**
         * Get [GroupExecutable] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [GroupExecutable].
         *
         * @throws IllegalArgumentException If argument not found or is not [GroupExecutable].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getGroupExecutable(name: String): GroupExecutable {
            return this.getArgument(name, GroupExecutable::class.java)
        }
    }

    override fun parse(reader: StringReader): GroupExecutable {
        val executable = GroupExecutable[reader.readString()] ?:
            throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return executable
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudServiceArgument].
     */
    fun notFound(message: String): GroupExecutableArgument {
        this.notFoundMessage = message

        return this
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        GroupExecutable.executables.forEach {
            if (it.key.contains(" ")) {
                builder.suggest("\"${it.key}\"")
            } else
                builder.suggest(it.key)
        }

        return builder.buildFuture()
    }
}