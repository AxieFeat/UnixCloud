package net.unix.api.event.impl.cloud

import net.unix.api.event.Cancellable
import net.unix.api.event.Event
import org.jline.reader.Candidate
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

class CloudTerminalCompleteEvent(
    val reader: LineReader,
    val line: ParsedLine,
    val candidates: MutableList<Candidate>
) : Event<CloudTerminalCompleteEvent>(), Cancellable {

    override var cancelled: Boolean = false

}