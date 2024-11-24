package net.unix.cloud.event.cloud

import net.unix.event.Event
import org.jline.reader.LineReader
import org.jline.utils.AttributedStringBuilder

/**
 * Calls when command highlighting.
 *
 * @param reader [LineReader] instance.
 * @param buffer Buffer.
 * @param builder [AttributedStringBuilder] instance.
 */
@Suppress("MemberVisibilityCanBePrivate")
class CloudTerminalHighlightEvent(
    val reader: LineReader,
    val buffer: String,
    val builder: AttributedStringBuilder
) : Event<CloudTerminalHighlightEvent>()