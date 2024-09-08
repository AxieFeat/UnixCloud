package net.unix.cloud.command.brigadier

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.context.ParsedCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.unix.api.CloudAPI
import net.unix.api.command.sender.CommandSender
import net.unix.cloud.CloudInstance
import net.unix.cloud.event.cloud.CloudTerminalHighlightEvent
import org.jline.reader.Highlighter
import org.jline.reader.LineReader
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import java.util.regex.Pattern
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
open class BrigadierCommandHighlighter(
    private val sender: CommandSender
) : Highlighter {

    override fun highlight(reader: LineReader, buffer: String): AttributedString {

        var builder = AttributedStringBuilder()

        val event = CloudTerminalHighlightEvent(reader, buffer, builder).callEvent()
        builder = event.builder

        val results: ParseResults<CommandSender> =
            CloudInstance.instance.commandDispatcher.dispatcher.parse(
                BrigadierCommandCompleter.prepareStringReader(buffer),
                sender
            )

        var pos = 0

        if (buffer.startsWith("/")) {
            builder.append("/", AttributedStyle.DEFAULT.foreground(6))
            pos = 1

            builder.append(buffer.substring(pos, buffer.length), AttributedStyle.DEFAULT)
            return builder.toAttributedString()
        }

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
                if (component >= COLORS.size) {
                    component = 0
                }

                builder.append(
                    buffer.substring(start, end), AttributedStyle.DEFAULT.foreground(
                        COLORS[component]
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

    private val COLORS = intArrayOf(6, 3, 2, 5, 4)
}
