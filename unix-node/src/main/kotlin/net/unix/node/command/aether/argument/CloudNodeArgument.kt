package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.node.Node
import net.unix.api.node.NodeManager
import net.unix.command.CommandArgument
import net.unix.node.command.aether.SyntaxExceptionBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Node]'s
 */
@Suppress("unused")
class CloudNodeArgument : CommandArgument<Node>(), KoinComponent {

    private val nodeManager: NodeManager by inject(named("default"))
    private var notFoundMessage = "Node not found"

    companion object {

        /**
         * Get [Node] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Node].
         *
         * @throws IllegalArgumentException If argument not found or is not [Node].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getNode(name: String): Node {
            return this.getArgument(name, Node::class.java)
        }
    }

    override fun parse(reader: StringReader): Node {
        return nodeManager[reader.readString()] ?:
        throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudServiceArgument].
     */
    fun notFound(message: String): CloudNodeArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): MutableCollection<String> {
        return nodeManager.nodes.map { it.name }.toMutableList()
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {

        nodeManager.nodes.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}