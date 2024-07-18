package net.unix.api.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.CloudAPI
import net.unix.api.command.aether.AetherArgument
import net.unix.api.command.aether.SyntaxExceptionBuilder
import net.unix.api.group.CloudGroup
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [CloudGroup]
 */
class CloudGroupArgument : AetherArgument<CloudGroup>() {

    private var notFoundMessage = "CloudGroup not found"

    companion object {
        /**
         * Get [CloudGroup] from command context by argument name
         *
         * @param name Argument name
         *
         * @return Instance of [CloudGroup]
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudGroup]
         */
        fun CommandContext<*>.getCloudGroup(name: String): CloudGroup {
            return this.getArgument(name, CloudGroup::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudGroup {
        val service = CloudAPI.instance.cloudGroupManager[reader.readString()]
            ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return service
    }

    /**
     * Set your own not found message
     *
     * @param message Message text
     *
     * @return Current instance of [CloudGroupArgument]
     */
    fun notFound(message: String): CloudGroupArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return CloudAPI.instance.cloudGroupManager.groups.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        CloudAPI.instance.cloudGroupManager.groups.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}