package net.unix.cloud.command.brigadier

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.StringReader
import net.unix.api.command.sender.CommandSender
import net.unix.cloud.event.callEvent
import net.unix.cloud.event.cloud.CloudTerminalCompleteEvent
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BrigadierCommandCompleter(
    private val sender: CommandSender
) : Completer {

    private val dispatcher: net.unix.api.command.CommandDispatcher by inject()

    companion object : KoinComponent {
        fun prepareStringReader(line: String): StringReader {
            val stringReader = StringReader(line)

            if (stringReader.canRead() && stringReader.peek() == '/') {
                stringReader.skip()
            }

            return stringReader
        }
    }

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {

        val event = CloudTerminalCompleteEvent(reader, line, candidates).callEvent()

        if (event.cancelled) {
            return
        }

        val dispatcher: CommandDispatcher<CommandSender> = dispatcher.dispatcher

        val stringLine = line.line()

        if (stringLine.startsWith("/")) return

        val results: ParseResults<CommandSender> = dispatcher.parse(
            prepareStringReader(stringLine),
            sender
        )

        val cursor = line.cursor()

        candidates.addAll(
            dispatcher.getCompletionSuggestions(results, cursor).join().list.map {
                Candidate(it.text)
            }
        )
    }
}