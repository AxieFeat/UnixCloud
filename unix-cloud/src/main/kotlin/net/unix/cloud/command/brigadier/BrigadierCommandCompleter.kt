package net.unix.cloud.command.brigadier

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.StringReader
import net.unix.api.CloudAPI
import net.unix.api.command.sender.CommandSender
import net.unix.api.event.impl.cloud.CloudTerminalCompleteEvent
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

open class BrigadierCommandCompleter(
    private val sender: CommandSender
) : Completer {

    companion object {
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

        val dispatcher: CommandDispatcher<CommandSender> = CloudAPI.instance.commandDispatcher.dispatcher

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