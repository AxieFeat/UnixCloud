package net.unix.node.command.question.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.node.Node
import net.unix.api.node.NodeManager
import net.unix.command.question.QuestionArgument
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

@Suppress("unused")
class QuestionNodeArgument : QuestionArgument<Node>, KoinComponent {

    private val nodeManager: NodeManager by inject()

    override fun parse(reader: StringReader): Node {
        return nodeManager[reader.readString()] ?: throw QuestionParseException("<red>Node not found!")
    }

    override fun suggestion(sender: CommandSender, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {

        nodeManager.nodes.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }

}