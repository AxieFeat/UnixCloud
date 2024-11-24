package net.unix.cloud.event.cloud

import net.unix.event.Event
import net.unix.event.Cancellable
import org.jline.reader.Candidate
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

/**
 * Calls when user complete suggestion.
 *
 * @param reader [LineReader] instance.
 * @param line Parsed line.
 * @param candidates Suggestions list.
 */
@Suppress("MemberVisibilityCanBePrivate")
class CloudTerminalCompleteEvent(
    val reader: LineReader,
    val line: ParsedLine,
    val candidates: MutableList<Candidate>
) : Event<CloudTerminalCompleteEvent>(), Cancellable {

    /**
     * Is cancelled
     */
    override var cancelled: Boolean = false

}