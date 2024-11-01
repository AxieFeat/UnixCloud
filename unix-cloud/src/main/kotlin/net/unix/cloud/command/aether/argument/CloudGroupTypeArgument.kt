package net.unix.cloud.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.command.CommandArgument
import net.unix.api.group.CloudGroupType
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [CloudGroupType].
 */
class CloudGroupTypeArgument : CommandArgument<CloudGroupType>() {

    private var notFoundMessage = "CloudGroupType not found"

    companion object {
        /**
         * Get [CloudGroupType] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [CloudGroupType].
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudGroupType].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudService(name: String): CloudGroupType {
            return this.getArgument(name, CloudGroupType::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudGroupType {
        val type = CloudGroupType[reader.readString()] ?:
            throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return type
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudServiceArgument].
     */
    fun notFound(message: String): CloudGroupTypeArgument {
        this.notFoundMessage = message

        return this
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        CloudGroupType.types.forEach {
            builder.suggest(it.key)
        }

        return builder.buildFuture()
    }
}