package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.api.group.Group
import net.unix.api.group.GroupManager
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Group].
 */
@Suppress("unused")
class CloudGroupArgument : CommandArgument<Group>(), KoinComponent {

    private val groupManager: GroupManager by inject(named("default"))
    private var notFoundMessage = "CloudGroup not found"

    companion object {

        /**
         * Get [Group] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Group].
         *
         * @throws IllegalArgumentException If argument not found or is not [Group].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudGroup(name: String): Group {
            return this.getArgument(name, Group::class.java)
        }
    }

    override fun parse(reader: StringReader): Group {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            groupManager[rawName].first().uuid
        }

        val group = groupManager[uuid]
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
        return groupManager.groups.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        groupManager.groups.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}