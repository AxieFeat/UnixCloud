package net.unix.cloud.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.api.group.CloudGroup
import net.unix.api.group.CloudGroupManager
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [CloudGroup].
 */
@Suppress("unused")
class CloudGroupArgument : CommandArgument<CloudGroup>() {

    private var notFoundMessage = "CloudGroup not found"

    companion object : KoinComponent {

        private val cloudGroupManager: CloudGroupManager by inject()

        /**
         * Get [CloudGroup] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [CloudGroup].
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudGroup].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudGroup(name: String): CloudGroup {
            return this.getArgument(name, CloudGroup::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudGroup {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            cloudGroupManager[rawName].first().uuid
        }

        val group = cloudGroupManager[uuid]
            ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return group
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudGroupArgument].
     */
    fun notFound(message: String): CloudGroupArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return cloudGroupManager.groups.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        cloudGroupManager.groups.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}