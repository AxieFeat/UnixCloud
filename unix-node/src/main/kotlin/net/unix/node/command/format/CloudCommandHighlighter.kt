package net.unix.node.command.format

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.ParsedCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.node.command.question.CloudQuestionManager
import net.unix.node.event.callEvent
import net.unix.node.event.cloud.CloudTerminalHighlightEvent
import net.unix.command.CommandDispatcher
import net.unix.command.question.exception.QuestionParseException
import net.unix.command.sender.CommandSender
import org.jline.reader.Highlighter
import org.jline.reader.LineReader
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.regex.Pattern
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
open class CloudCommandHighlighter(
    private val sender: CommandSender
) : Highlighter, KoinComponent {

    private val dispatcher: CommandDispatcher by inject(named("default"))

    override fun highlight(reader: LineReader, buffer: String): AttributedString {

        var builder = AttributedStringBuilder()

        val event = CloudTerminalHighlightEvent(reader, buffer, builder).callEvent()
        builder = event.builder

        val activeQuestion = CloudQuestionManager.activeQuestion

        if(activeQuestion != null) {
            val answerType = activeQuestion.argument

            try {
                answerType.parse(StringReader(buffer))
                builder.append(buffer, AttributedStyle.DEFAULT)
            } catch (e: QuestionParseException) {
                builder.append(buffer, AttributedStyle.DEFAULT.foreground(1))
            }

            return builder.toAttributedString()
        }

        var pos = 0

        if (buffer.startsWith("/")) {
            builder.append("/", AttributedStyle.DEFAULT.foreground(6))
            pos = 1

            builder.append(buffer.substring(pos, buffer.length), AttributedStyle.DEFAULT)
            return builder.toAttributedString()
        }

        val results: ParseResults<CommandSender> =
            dispatcher.dispatcher.parse(
                CloudCommandCompleter.prepareStringReader(buffer),
                sender
            )

        var component = -1

        var end: Int
        val iterator: Iterator<*> = results.context.lastChild.nodes.iterator()

        while (iterator.hasNext()) {

            val node: ParsedCommandNode<CommandSender> = iterator.next() as ParsedCommandNode<CommandSender>
            if (node.range.start >= buffer.length) {
                break
            }

            val start = node.range.start
            end = min(node.range.end.toDouble(), buffer.length.toDouble()).toInt()

            builder.append(buffer.substring(pos, start), AttributedStyle.DEFAULT)

            if (node.node is LiteralCommandNode<*>) {

                builder.append(buffer.substring(start, end), AttributedStyle.DEFAULT)

            } else {

                component++
                if (component >= colors.size) {
                    component = 0
                }

                builder.append(
                    buffer.substring(start, end), AttributedStyle.DEFAULT.foreground(
                        colors[component]
                    )
                )

            }
            pos = end
        }

        if (pos < buffer.length) {
            builder.append(buffer.substring(pos), AttributedStyle.DEFAULT.foreground(1))
        }

        return builder.toAttributedString()
    }

    override fun setErrorPattern(p0: Pattern?) {

    }

    override fun setErrorIndex(p0: Int) {

    }

    private val colors = intArrayOf(6, 3, 2, 5, 4)

    companion object
}
