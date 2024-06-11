package net.unix.api.event.impl.cloud

import net.unix.api.event.Event
import org.jline.reader.LineReader
import org.jline.utils.AttributedStringBuilder

class CloudTerminalHighlightEvent(
    val reader: LineReader,
    val buffer: String,
    val builder: AttributedStringBuilder
) : Event<CloudTerminalHighlightEvent>()